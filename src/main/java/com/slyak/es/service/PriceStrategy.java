package com.slyak.es.service;

import java.math.BigDecimal;
import java.util.List;

public interface PriceStrategy {

    PriceStrategy init(StrategyArgs args);

    List<BigDecimal> genPriceSteps();
}
