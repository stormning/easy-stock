package com.slyak.es.config;

import com.google.common.collect.Maps;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ScheduledTaskService implements ApplicationListener<ApplicationReadyEvent> {
    private final Map<String, ScheduledTask> taskMap = Maps.newHashMap();

    private final List<TaskInfo> taskInfos = new ArrayList<>();

    private final ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    public ScheduledTaskService(ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor) {
        this.scheduledAnnotationBeanPostProcessor = scheduledAnnotationBeanPostProcessor;
    }

    public List<TaskInfo> getTaskInfos() {
        return taskInfos;
    }

    public void runTask(String name) {
        taskMap.get(name).getTask().getRunnable().run();
    }

    public void initTaskInfo(Set<ScheduledTask> scheduledTasks) {
        for (ScheduledTask scheduledTask : scheduledTasks) {
            Runnable runnable = scheduledTask.getTask().getRunnable();
            if (runnable instanceof ScheduledMethodRunnable) {
                ScheduledMethodRunnable scheduledMethodRunnable = (ScheduledMethodRunnable) runnable;
                Method method = scheduledMethodRunnable.getMethod();
                Scheduled scheduledAnnotation = AnnotationUtils.findAnnotation(method, Scheduled.class);
                if (scheduledAnnotation != null) {
                    TaskInfo taskInfo = new TaskInfo();
                    String className = method.getDeclaringClass().getName();
                    String methodName = method.getName();
                    String taskName = className + "#" + methodName;
                    taskInfo.setTaskName(taskName);
                    taskInfo.setMethodName(method.getName());
                    taskInfo.setClassName(className);
                    taskInfo.setCronExpression(scheduledAnnotation.cron());
                    taskInfos.add(taskInfo);
                    taskMap.put(taskName, scheduledTask);
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Set<ScheduledTask> scheduledTasks = scheduledAnnotationBeanPostProcessor.getScheduledTasks();
        initTaskInfo(scheduledTasks);
    }
}

