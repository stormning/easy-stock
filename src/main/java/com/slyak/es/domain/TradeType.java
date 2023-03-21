package com.slyak.es.domain;

public enum TradeType implements Titleable{
    BUY("买入"),SELL("卖出");

    private String title;

    TradeType(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
