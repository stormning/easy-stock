package com.slyak.es.service.strategy;

import com.slyak.es.domain.PriceAndAmount;
import com.slyak.es.service.SellStrategy;
import com.slyak.es.service.StrategyArgs;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

//目标价值（估值）卖出法
@Data
public class TargetValueStrategy implements PercentageSellStrategy {

    //每上涨6%
    private BigDecimal peLow;

    //卖出6*0.618 = 3.6
    private BigDecimal peHigh;

    @Override
    public SellStrategy init(StrategyArgs args) {
        return null;
    }

    @Override
    public List<PriceAndAmount> generate() {
        return null;
    }
}