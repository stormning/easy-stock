package com.slyak.es.service.impl;

import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskCompletionHandlerRegistry {
    private Map<Class<? extends Persistable<Long>>, TaskCompletionHandler<?>> handlerMap = new HashMap<>();

    public <T extends Persistable<Long>> void registerHandler(Class<T> entityType, TaskCompletionHandler<T> handler) {
        handlerMap.put(entityType, handler);
    }

    public <T extends Persistable<Long>> TaskCompletionHandler<T> getHandler(Class<T> entityType) {
        return (TaskCompletionHandler<T>) handlerMap.get(entityType);
    }
}
