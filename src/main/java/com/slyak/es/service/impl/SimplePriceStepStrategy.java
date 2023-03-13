package com.slyak.es.service.impl;

import com.google.common.collect.Lists;
import com.slyak.es.service.PriceStrategy;
import com.slyak.es.service.StrategyArgs;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 起始、终止价等额梯度策略
 */
@Data
public class SimplePriceStepStrategy implements PriceStrategy {

    private BigDecimal startPrice;
    private BigDecimal endPrice;
    private BigDecimal gradient;
    private List<BigDecimal> priceSteps = Lists.newArrayList();

    @Override
    public PriceStrategy init(StrategyArgs args) {
        this.startPrice = args.getBigDecimal("startPrice");
        this.endPrice = args.getBigDecimal("endPrice");
        this.gradient = args.getBigDecimal("gradient");
        init();
        return this;
    }

    private void init() {
        int count = BigDecimal.valueOf(100).divide(gradient, 0, RoundingMode.HALF_UP).intValue();
        BigDecimal nextPrice = startPrice;
        BigDecimal step = endPrice.subtract(startPrice).multiply(gradient).divide(BigDecimal.valueOf(100), startPrice.scale(), RoundingMode.HALF_UP);
        for (int i = 0; i <= count; i++) {
            priceSteps.add(nextPrice);
            nextPrice = nextPrice.add(step);
        }
    }

    @Override
    public List<BigDecimal> genPriceSteps() {
        return priceSteps;
    }
}