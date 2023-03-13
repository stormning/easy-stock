package com.slyak.es.service.impl;

import com.google.common.collect.Lists;
import com.slyak.es.constant.Constants;
import com.slyak.es.domain.*;
import com.slyak.es.hibernate.assembler.EntityAssemblers;
import com.slyak.es.repo.PlanItemRepo;
import com.slyak.es.repo.PlanRepo;
import com.slyak.es.repo.TradeRepo;
import com.slyak.es.service.PlanService;
import com.slyak.es.service.StockService;
import com.slyak.es.util.JpaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanServiceImpl implements PlanService {

    private PlanRepo planRepo;

    private PlanItemRepo planItemRepo;

    private TradeRepo tradeRepo;

    private StockService stockService;

    @Autowired
    public PlanServiceImpl(PlanRepo planRepo, PlanItemRepo planItemRepo, StockService stockService) {
        this.planRepo = planRepo;
        this.planItemRepo = planItemRepo;
        this.stockService = stockService;
    }

    @Override
    public Plan getById(Long id) {
        Optional<Plan> byId = planRepo.findById(id);
        if (byId.isPresent()) {
            Plan plan = byId.get();
            EntityAssemblers entityAssemblers = EntityAssemblers.newInstance();
            entityAssemblers.assemble(plan.getStock());
            return plan;
        } else {
            return null;
        }
    }

    @Override
    public Plan init(String stockCode) {
        Assert.hasText(stockCode, "股票代码不能为空");
        Stock stock = stockService.getStock(stockCode);
        Assert.notNull(stock, "无效的股票代码");
        List<Plan> plans = queryPlans(stock);
        Assert.isTrue(CollectionUtils.isEmpty(plans), "持仓计划已存在");
        Plan plan = new Plan();
        plan.setStock(stock);
        planRepo.save(plan);
        return plan;
    }

    @Override
    public List<Plan> queryPlans(Stock stock) {
        List<Plan> plans = planRepo.findAll(JpaUtil.getQuerySpecification(new Plan().setStock(stock)));
        if (!CollectionUtils.isEmpty(plans)) {
            List<Stock> stocks = plans.stream().map(Plan::getStock).collect(Collectors.toList());
            EntityAssemblers.newInstance().assemble(stocks);
        }
        return plans;
    }

    @Override
    public List<PlanItem> getPlanItems(Long planId) {
        return planItemRepo.findPlanItemsByPlanId(planId);
    }

    @Override
    public List<PlanItem> genPlanItems(Long planId, List<BigDecimal> prices, BigDecimal startCost, BigDecimal supplement) {
        List<PlanItem> items = Lists.newArrayList();
        BigDecimal startPrice = prices.get(0);
        //one hand ... emm ...
        BigDecimal oneHand = startPrice.multiply(BigDecimal.valueOf(100));
        Assert.isTrue(startCost.compareTo(oneHand) >= 0, "起手最大金额至少大于一手金额");
        int firstAmount = startCost.divide(oneHand, 0, RoundingMode.FLOOR).intValue() * 100;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.valueOf(100).subtract(supplement).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        for (int i = 0; i < prices.size(); i++) {
            BigDecimal price = prices.get(i);
            PlanItem item = new PlanItem();
            item.setPlanId(planId);
            int amount;
            BigDecimal cost;
            if (i == 0) {
                amount = firstAmount;
            } else {
                // 总成本 * 折损率 = 总市值
                // (购买数*当前价 + 之前的成本)* 折扣 = (购买数 + 之前的数量) * 当前价
                // 购买数*当前价*折扣 + 之前的成本*折扣 = 购买数 * 当前价 + 之前的数量 * 当前价
                // 购买数 * 当前价 * (1-折扣) = 之前的成本*折扣 - 之前的数量 * 当前价
                // 购买数 = (之前的成本*折扣 - 之前的数量 * 当前价)/[当前价*(1-折扣)]
                //// n = (totalCost*discount - price*totalAmount)/(price-price*discount)
                amount = totalCost.multiply(discount).subtract(price.multiply(totalAmount))
                        .divide(price.subtract(price.multiply(discount)), 0, RoundingMode.FLOOR)
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).intValue();
            }
            if (amount <= 0) {
                continue;
            }

            cost = price.multiply(BigDecimal.valueOf(amount));
            BigDecimal costWithFee = Constants.trade(cost, new BigDecimal("0.0001"), TradeType.BUY);
            totalCost = totalCost.add(costWithFee);
            totalAmount = totalAmount.add(BigDecimal.valueOf(amount));
            item.setCost(costWithFee);
            item.setAmount(amount);
            item.setPrice(price);

            items.add(item);
        }
        planItemRepo.saveAll(items);
        return items;
    }

    @Override
    @Transactional
    public void savePlanItems(List<PlanItem> items) {
        planItemRepo.saveAll(items);
    }

    // 目前平均成本 * 容忍折损率 = 新购价格
    // (turnOver + price *n) * partLose/ (amount+n) = price
    // turnOver*partLose + price*partLose* n = price*amount + price*n
    // n = (turnOver*partLose - price*amount)/(price-price*partLose)
    private Trade nextTrade(long amount, BigDecimal lastPrice, BigDecimal turnover, BigDecimal marginStep, BigDecimal partLose) {
        BigDecimal price = lastPrice.subtract(marginStep);
        BigDecimal numerator = turnover.multiply(partLose).subtract(price.multiply(BigDecimal.valueOf(amount)));
        BigDecimal denominator = price.subtract(price.multiply(partLose));
        long amountToBuy = numerator.divide(denominator, 0, RoundingMode.CEILING).longValue();
        amountToBuy = (long) (Math.ceil(amountToBuy / 100.0) * 100);
        return new Trade().setAmount(amountToBuy).setPrice(price);
    }

    //建仓
    public Trade getFirstTrade(Plan plan, BigDecimal commission) {
        BigDecimal capital = plan.getCapital();
        BigDecimal marginStep = plan.getMarginStep();
        int amount = 0;
        BigDecimal turnover;
        do {
            amount += 100;
            int total = amount;
            BigDecimal lastPrice = plan.getFirstPrice();
            turnover = BigDecimal.ZERO.subtract(Constants.trade(lastPrice.multiply(BigDecimal.valueOf(amount)), commission, TradeType.BUY));
            int marginCnt = BigDecimal.valueOf(100).divide(plan.getLosePercent(), 1, RoundingMode.CEILING).intValue();
            for (int i = 0; i < marginCnt + 1; i++) {
                //多算一次
                BigDecimal partLose = getMarginLose(marginStep, lastPrice);
                Trade trade = nextTrade(total, lastPrice, turnover, marginStep, partLose).init(plan, commission, TradeType.BUY);
                lastPrice = trade.getPrice();
                total += trade.getAmount();
                turnover = turnover.subtract(trade.getTurnover());
                System.out.println(trade);
            }
        } while (turnover.compareTo(capital) < 0);
        return new Trade().setPrice(plan.getFirstPrice()).setAmount(amount).init(plan, commission, TradeType.BUY);
    }

    //补仓到损失多少百分比
    public BigDecimal getMarginLose(BigDecimal marginStep, BigDecimal lastPrice) {
        return BigDecimal.valueOf(1).subtract(marginStep.divide(lastPrice.multiply(BigDecimal.valueOf(2)), 2, RoundingMode.CEILING));
    }

    public static void main(String[] args) {
        PlanServiceImpl planService = new PlanServiceImpl(null, null, null);
        Plan plan = new Plan();
        plan.setCapital(BigDecimal.valueOf(1000));
        plan.setFirstPrice(BigDecimal.valueOf(0.822));
        plan.setWorstPrice(BigDecimal.valueOf(0.411));
        plan.setLosePercent(BigDecimal.valueOf(20));
        plan.setWinPercent(BigDecimal.valueOf(20));
        Trade firstTrade = planService.getFirstTrade(plan, BigDecimal.valueOf(0.00015));
        System.out.println(firstTrade);
    }
}
