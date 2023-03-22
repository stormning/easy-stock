package com.slyak.es.service;

import com.slyak.es.domain.Task;
import com.slyak.es.domain.TaskStatus;
import com.slyak.es.domain.User;

import java.util.List;

public interface TaskService {

    Task createTask(TaskContentMaker<?> contentMaker, Class<?> relClass, Long relId, User user);

    Task saveTask(Task task);

    void deleteTask(Long taskId);

    List<Task> getUserTasks(TaskStatus status);

    Task getTaskById(Long taskId);

    void completeTask(Long taskId);

    void clearAll();

    void startTask(Long id);
}
