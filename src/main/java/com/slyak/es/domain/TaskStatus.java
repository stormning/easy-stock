package com.slyak.es.domain;

public enum TaskStatus implements Titleable{
    PENDING("待完成"),
    COMPLETED("已完成");

    private final String title;

    TaskStatus(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
