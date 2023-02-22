package com.slyak.es.domain;

public enum OrgType implements Titleable{
    GP("合伙"), QDII("QDII");

    private final String title;

    OrgType(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
