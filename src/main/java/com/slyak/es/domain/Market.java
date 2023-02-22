package com.slyak.es.domain;

public enum Market implements Titleable{
    sh("沪"), sz("深");

    private final String title;

    Market(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
