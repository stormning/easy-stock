package com.slyak.es.service;

public interface TaskContentMaker<T> {
    T deserialize(String content);

    String serialize();

    String getTitle();
}
