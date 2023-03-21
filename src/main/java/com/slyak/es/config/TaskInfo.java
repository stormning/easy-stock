package com.slyak.es.config;

import lombok.Data;

@Data
public class TaskInfo {
    private String taskName;
    private String className;
    private String methodName;
    private String cronExpression;

}
