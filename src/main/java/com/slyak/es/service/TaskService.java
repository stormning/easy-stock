package com.slyak.es.service;

import com.slyak.es.domain.Task;

import java.util.List;

public interface TaskService {

    Task createTask(TaskContentMaker<?> contentMaker, Class<?> relClass, Long relId);

    Task saveTask(Task task);

    void deleteTask(Long taskId);

    List<Task> getAllTasks();

    Task getTaskById(Long taskId);

    void completeTask(Long taskId);

    void clearAll();
}
