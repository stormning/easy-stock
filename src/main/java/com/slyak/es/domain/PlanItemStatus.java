package com.slyak.es.domain;

public enum PlanItemStatus implements Titleable{
    WAIT("待完成"), FINISH("已完成");

    private final String title;

    PlanItemStatus(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
