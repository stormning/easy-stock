package com.slyak.es.service.impl;

import com.slyak.es.constant.Constants;
import com.slyak.es.domain.Plan;
import com.slyak.es.domain.Trade;
import com.slyak.es.domain.TradeType;
import com.slyak.es.repo.PlanRepo;
import com.slyak.es.repo.TradeRepo;
import com.slyak.es.service.PlanService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    private PlanRepo planRepo;

    private TradeRepo tradeRepo;

    public PlanServiceImpl(PlanRepo planRepo) {
        this.planRepo = planRepo;
    }

    @Override
    public Plan getById(Long id) {
        return planRepo.getById(id);
    }

    public List<Trade> getNextTrades(Long planId) {
        Plan plan = getById(planId);
        BigDecimal capital = plan.getCapital();

        BigDecimal firstPrice = plan.getFirstPrice();

        int scale = firstPrice.scale();

        BigDecimal lastPrice = plan.getLastPrice();
        BigDecimal worstPrice = plan.getWorstPrice();


        BigDecimal turnover = plan.getTurnover();
        long amount = plan.getAmount();
        //如果amount为0，则需要计算出第一次的amount，随着amount增加，补仓
        if (amount == 0) {
            //第一次价格 + 第二次次价格 + ... 第n次价格 <= 总成本数
            //n<=补仓总次数

        }


        //止损即为加仓步长
        BigDecimal losePercent = plan.getLosePercent();
        //补仓后残值(1-止损位的中位值)
        BigDecimal partLose = BigDecimal.valueOf(1).subtract(losePercent.divide(BigDecimal.valueOf(200), 2, RoundingMode.HALF_UP));

        //firstPrice lastPrice worstPrice
        BigDecimal marginStep = firstPrice.subtract(worstPrice).multiply(losePercent).divide(BigDecimal.valueOf(100), scale, RoundingMode.HALF_UP);

        int count = lastPrice.subtract(worstPrice).divide(marginStep, 0, RoundingMode.CEILING).intValue();
        int cnt = 1;


        return null;
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
        PlanServiceImpl planService = new PlanServiceImpl(null);
        Plan plan = new Plan();
        plan.setCapital(BigDecimal.valueOf(1000));
        plan.setFirstPrice(BigDecimal.valueOf(4.99));
        plan.setWorstPrice(BigDecimal.valueOf(2.5));
        plan.setLosePercent(BigDecimal.valueOf(10));
        plan.setWinPercent(BigDecimal.valueOf(5));
        Trade firstTrade = planService.getFirstTrade(plan, BigDecimal.valueOf(0.00015));
        System.out.println(firstTrade);
    }
}
