package com.slyak.es.service;

import com.slyak.es.domain.Task;
import org.springframework.data.domain.Persistable;

import java.util.List;

public interface TaskService<T extends Persistable<Long>> {

    Task<T> createTask(Task<T> task);

    Task<T> saveTask(Task<T> task);

    void deleteTask(Long taskId);

    List<Task<T>> getAllTasks();

    Task<T> getTaskById(Long taskId);

    void completeTask(Long taskId);
}
