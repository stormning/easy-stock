package com.slyak.es.service.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.slyak.es.domain.*;
import com.slyak.es.service.PlanService;
import com.slyak.es.service.SellStrategy;
import com.slyak.es.service.StrategyArgs;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;


//目标价格卖出法
@Data
public class TargetPriceStrategy implements PercentageSellStrategy {

    @Autowired
    private final PlanService planService;

    private Stock stock;

    private Long planId;

    @Override
    public SellStrategy init(StrategyArgs args) {
        Long planId = args.getPlanId();
        this.planId = planId;
        this.stock = planService.getById(planId).getStock();
        return this;
    }

    @Override
    public List<PriceAndAmount> generate() {
        BigDecimal curPrice = stock.getInfo().getFixedPrice();
        List<PriceAndAmount> result = Lists.newArrayList();
        List<PlanItem> items = planService.getPlanItems(planId).stream().filter(planItem -> PlanItemStatus.SELL_STATUS.contains(planItem.getStatus())).map(PlanItem::copy).collect(Collectors.toList());
        AmountAndAvgPrice amountAndAvgPrice = new AmountAndAvgPrice(items).build();
        loopGen(curPrice, result, amountAndAvgPrice);
        return result;
    }

    private void loopGen(BigDecimal curPrice, List<PriceAndAmount> result, AmountAndAvgPrice amountAndAvgPrice) {
        BigDecimal rise = getRise();
        BigDecimal sell = getSell();
        long amount = amountAndAvgPrice.getAmount();
        BigDecimal avgPrice = amountAndAvgPrice.getAvgPrice();
        BigDecimal targetPrice = avgPrice.multiply(BigDecimal.valueOf(100).add(rise)).divide(BigDecimal.valueOf(100), avgPrice.scale(), RoundingMode.HALF_UP);
        BigDecimal maxPrice = curPrice.multiply(BigDecimal.valueOf(100).add(stock.getMaxPercent())).divide(BigDecimal.valueOf(100), curPrice.scale(), RoundingMode.FLOOR);
        if (Range.closed(curPrice, maxPrice).contains(targetPrice)) {
            long sellAmount = sell.multiply(BigDecimal.valueOf(amount)).divide(BigDecimal.valueOf(10000), RoundingMode.FLOOR).multiply(BigDecimal.valueOf(100)).longValue();
            if (sellAmount > 0) {
                PriceAndAmount priceAndAmount = new PriceAndAmount();
                priceAndAmount.setSellPrice(targetPrice);
                priceAndAmount.setSellAmount(sellAmount);
                result.add(priceAndAmount);
                amountAndAvgPrice.sell(sellAmount);
                loopGen(curPrice, result, amountAndAvgPrice.build());
            }
        }
    }


    @Data
    public static class AmountAndAvgPrice {
        private long amount = 0;

        private BigDecimal cost = BigDecimal.ZERO;

        private List<PlanItem> planItems;


        public AmountAndAvgPrice(List<PlanItem> planItems) {
            this.planItems = planItems;
        }

        public AmountAndAvgPrice build(){
            for (PlanItem planItem : planItems) {
                cost = cost.add(planItem.getCost());
                amount = amount + planItem.getAmount();
            }
            return this;
        }

        public BigDecimal getAvgPrice() {
            return cost.divide(BigDecimal.valueOf(amount), cost.scale(), RoundingMode.CEILING);
        }

        public void sell(long amount) {
            long remain = amount;
            int n = planItems.size();
            for (int i = 0; i < planItems.size(); i++) {
                PlanItem planItem = planItems.get(i);
                long itemSellAmount = Math.min(planItem.getLeftAmount(), amount);
                remain = amount - itemSellAmount;
                if (remain == 0) {
                    break;
                } else {
                    planItems.remove(i);
                    i--;
                    n--;
                    planItem.subtract(itemSellAmount);
                }
            }
        }
    }

}