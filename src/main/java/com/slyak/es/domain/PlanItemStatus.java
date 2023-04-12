package com.slyak.es.domain;

import com.google.common.collect.Sets;

import java.util.Set;

public enum PlanItemStatus implements Titleable{
    WAIT("待完成"), FINISH("已完成"), PART_FINISH("部成");

    public static Set<PlanItemStatus> BUY_STATUS = Sets.newHashSet(WAIT, PART_FINISH);

    public static Set<PlanItemStatus> SELL_STATUS = Sets.newHashSet(FINISH, PART_FINISH);

    private final String title;

    PlanItemStatus(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
