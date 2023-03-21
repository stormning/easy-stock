package com.slyak.es.service.impl;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskCompletionHandlerRegistry {
    private Map<Class<?>, TaskCompletionHandler> handlerMap = new HashMap<>();

    public void registerHandler(Class<?> entityType, TaskCompletionHandler handler) {
        handlerMap.put(entityType, handler);
    }

    public  TaskCompletionHandler getHandler(Class<?> entityType) {
        return handlerMap.get(entityType);
    }
}
