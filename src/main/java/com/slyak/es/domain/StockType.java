package com.slyak.es.domain;

public enum StockType implements Titleable {
    A("A股"), LOF("LOF"), ETF("ETF");

    private final String title;

    StockType(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
