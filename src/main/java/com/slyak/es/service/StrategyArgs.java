package com.slyak.es.service;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Map;

public class StrategyArgs {

    private final Long planId;
    private Map<String, String> args;

    public StrategyArgs(Long planId, Map<String, String> args) {
        this.planId = planId;
        this.args = args;
    }

    public Long getPlanId() {
        return planId;
    }

    public BigDecimal getBigDecimal(String name) {
        return NumberUtils.createBigDecimal(getString(name));
    }

    public String getString(String name){
        return args.get(name);
    }

    public Integer getInteger(String name){
        return NumberUtils.toInt(getString(name));
    }

    public Long getLong(String name){
        return NumberUtils.toLong(getString(name));
    }
}
