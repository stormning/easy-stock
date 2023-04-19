package com.slyak.es.service.strategy;

import com.slyak.es.service.SellStrategy;

import java.math.BigDecimal;

import static com.slyak.es.constant.Constants.GOLDEN_PERCENT;

public interface PercentageSellStrategy extends SellStrategy {
    default BigDecimal getRise() {
        return GOLDEN_PERCENT.multiply(BigDecimal.valueOf(10));
    }

    default BigDecimal getSell() {
        return getRise().multiply(GOLDEN_PERCENT);
    }
}
