package com.slyak.es.service;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class StrategyRequest {
    String className;

    Map<String, String> args;

    Long planId;

    /**
     * 起手建仓金额
     */
    private BigDecimal startCost;

    /**
     * 补仓至亏损幅度
     */
    private BigDecimal supplement;

    @SneakyThrows
    public PriceStrategy initStrategy() {
        Class<?> aClass = ClassUtils.forName(className, this.getClass().getClassLoader());
        Constructor<?> constructor = aClass.getConstructor();
        PriceStrategy priceStrategy = (PriceStrategy) constructor.newInstance();
        return priceStrategy.init(new StrategyArgs(planId, args));
    }
}
