package com.slyak.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slyak.es.config.security.SecurityUtils;
import com.slyak.es.constant.Constants;
import com.slyak.es.domain.*;
import com.slyak.es.hibernate.assembler.EntityAssemblers;
import com.slyak.es.repo.PlanItemRepo;
import com.slyak.es.repo.PlanRepo;
import com.slyak.es.repo.PlanSellStrategyRepo;
import com.slyak.es.repo.TradeRepo;
import com.slyak.es.service.*;
import com.slyak.es.service.strategy.TargetPriceStrategy;
import com.slyak.es.util.JpaUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanServiceImpl implements PlanService, TaskCompletionHandler, InitializingBean, ApplicationContextAware {

    private final PlanRepo planRepo;

    private final PlanItemRepo planItemRepo;

    private final TradeRepo tradeRepo;

    private final StockService stockService;

    private final TaskCompletionHandlerRegistry registry;

    private final PlanSellStrategyRepo planSellStrategyRepo;
    private ApplicationContext applicationContext;

    @Autowired
    public PlanServiceImpl(PlanRepo planRepo, PlanItemRepo planItemRepo, TradeRepo tradeRepo, StockService stockService, TaskCompletionHandlerRegistry registry, PlanSellStrategyRepo planSellStrategyRepo) {
        this.planRepo = planRepo;
        this.planItemRepo = planItemRepo;
        this.tradeRepo = tradeRepo;
        this.stockService = stockService;
        this.registry = registry;
        this.planSellStrategyRepo = planSellStrategyRepo;
    }

    @Override
    public Plan getById(Long id) {
        Plan plan = getByIdInternal(id);
        if (plan != null) {
            EntityAssemblers entityAssemblers = EntityAssemblers.newInstance();
            entityAssemblers.assemble(plan.getStock());
        }
        return plan;
    }

    private Plan getByIdInternal(Long id) {
        return planRepo.findById(id).orElse(null);
    }

    @Override
    public Plan init(String stockCode) {
        Assert.hasText(stockCode, "股票代码不能为空");
        Stock stock = stockService.getStock(stockCode);
        Assert.notNull(stock, "无效的股票代码");
        List<Plan> plans = queryUserPlans(SecurityUtils.getUser(), stock);
        Assert.isTrue(CollectionUtils.isEmpty(plans), "持仓计划已存在");
        Plan plan = new Plan();
        plan.setStock(stock);
        planRepo.save(plan);
        return plan;
    }

    @Override
    public List<Plan> queryUserPlans(User user, Stock stock) {
        Plan plan = new Plan().setStock(stock);
        plan.setCreatedBy(user);
        List<Plan> plans = planRepo.findAll(JpaUtil.getQuerySpecification(plan, Sort.by(Sort.Direction.DESC, "cost")));
        if (!CollectionUtils.isEmpty(plans)) {
            List<Stock> stocks = plans.stream().map(Plan::getStock).collect(Collectors.toList());
            EntityAssemblers.newInstance().assemble(stocks);
        }
        return plans;
    }

    @Override
    public List<PlanItem> getPlanItems(Long planId) {
        return planItemRepo.findPlanItemsByPlanIdOrderByPriceDesc(planId);
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
//            BigDecimal cost;
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
            BigDecimal costWithFee = getCostWithFee(amount, price, TradeType.BUY);
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

    private BigDecimal getCostWithFee(long amount, BigDecimal price, TradeType tt) {
        BigDecimal cost = price.multiply(BigDecimal.valueOf(amount));
        return Constants.trade(cost, new BigDecimal("0.0001"), tt);
    }

    private PlanItem resetCost(PlanItem planItem) {
        planItem.setCost(getCostWithFee(planItem.getAmount(), planItem.getPrice(), TradeType.BUY));
        return planItem;
    }

    @Override
    @Transactional
    public void savePlanItems(List<PlanItem> items) {
        for (PlanItem item : items) {
            savePlanItem(item);
        }
    }

    @Override
    @Transactional
    public PlanItem savePlanItem(PlanItem planItem) {
        return planItemRepo.save(resetCost(planItem));
    }

    @Override
    @Transactional
    public void deletePlanItem(Long id) {
        Optional<PlanItem> planItemOptional = planItemRepo.findById(id);
        if (planItemOptional.isPresent()) {
            PlanItem planItem = planItemOptional.get();
            Assert.isTrue(planItem.getStatus() == PlanItemStatus.WAIT, "已完成的项不允许删除");
            planItemRepo.delete(planItem);
        }
    }

    @Override
    @Transactional
    public void finishItem(Long id) {
        Optional<PlanItem> planItemOptional = planItemRepo.findById(id);
        if (planItemOptional.isPresent()) {
            PlanItem planItem = planItemOptional.get();
            planItem.setStatus(PlanItemStatus.FINISH);
            //TODO amount as an argument
            //realAmount
            planItem.setRealAmount(planItem.getAmount());
            savePlanItem(planItem);
            restPlanSummary(planItem.getPlanId());
        }
    }

    private void restPlanSummary(Long planId) {
        Plan plan = planRepo.getReferenceById(planId);
        List<PlanItem> items = planItemRepo.findByPlanId(planId);
        long amount = 0;
        BigDecimal cost = BigDecimal.ZERO;
        for (PlanItem item : items) {
            if (PlanItemStatus.SELL_STATUS.contains(item.getStatus())) {
                amount += item.getRealAmount();
                cost = cost.add(item.getCost());
            }
        }
        plan.setAmount(amount);
        plan.setCost(cost);
        planRepo.save(plan);
    }

    @Override
    @Transactional
    public void deletePlanItems(Long id) {
        Plan plan = getById(id);
        if (plan != null) {
            planItemRepo.deleteByPlanId(plan.getId());
            plan.setAmount(0);
            plan.setCost(BigDecimal.ZERO);
            planRepo.save(plan);
        }
    }

    @Override
    @Transactional
    public void sellPlanItem(Long planId, long amount) {
        List<PlanItem> items = planItemRepo.findByPlanId(planId).stream()
                .filter(planItem -> PlanItemStatus.SELL_STATUS.contains(planItem.getStatus()))
                .sorted(Comparator.comparing(PlanItem::getPrice))
                .collect(Collectors.toList());

        for (PlanItem item : items) {
            PlanItemStatus status = item.getStatus();
            if (PlanItemStatus.SELL_STATUS.contains(status)) {
                long sellAmount = Math.min(amount, item.getRealAmount());
                long left = item.getRealAmount() - sellAmount;
                BigDecimal income = getCostWithFee(sellAmount, item.getPrice(), TradeType.SELL);
                item.setCost(item.getCost().subtract(income));
                item.setRealAmount(left);
                if (left == 0) {
                    item.setStatus(PlanItemStatus.WAIT);
                } else {
                    item.setStatus(PlanItemStatus.PART_FINISH);
                }
                amount = amount - sellAmount;
                planItemRepo.save(item);
                if (amount == 0) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    @Transactional
    public void handleCompletion(Task task) {
        String content = task.getContent();
        TradeTaskContent tc = JSON.parseObject(content, TradeTaskContent.class);
        TradeType tradeType = tc.getTradeType();
        Long id = task.getRelatedEntityId();
        if (tradeType == TradeType.BUY) {
            //TODO add trade of type buy
            finishItem(id);
        } else {
            //sell
            //plan id
            BigDecimal price = tc.getPrice();
            long amount = tc.getAmount();
            sellPlanItem(id, amount);
        }
    }

    private static final PlanSellStrategy DEFAULT_PLAN_SELL_STRATEGY = new PlanSellStrategy();

    static {
        DEFAULT_PLAN_SELL_STRATEGY.setClassName(TargetPriceStrategy.class.getName());
    }

    @Override
    @SneakyThrows
    public SellStrategy getSellStrategy(Long planId) {
        PlanSellStrategy pss = planSellStrategyRepo.findByPlanId(planId);
        if (pss == null) {
            pss = DEFAULT_PLAN_SELL_STRATEGY;
        }
        Class<?> aClass = ClassUtils.forName(pss.getClassName(), ClassUtils.getDefaultClassLoader());
        SellStrategy ss = (SellStrategy) ConstructorUtils.invokeConstructor(aClass);
        Map<String, String> params = Maps.newHashMap(pss.getParams());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(ss);
        ss.init(new StrategyArgs(planId, params));
        return ss;
    }

    @Override
    public void afterPropertiesSet() {
        registry.registerHandler(PlanItem.class, this);
        registry.registerHandler(Plan.class, this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
