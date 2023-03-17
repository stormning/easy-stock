package com.slyak.es.service.impl;

import com.slyak.es.domain.Task;
import com.slyak.es.domain.TaskStatus;
import com.slyak.es.repo.TaskRepository;
import com.slyak.es.service.TaskService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl<T extends Persistable<Long>> implements TaskService<T>{

    @Autowired
    private TaskRepository<T> taskRepository;

    @Autowired
    private TaskCompletionHandlerRegistry handlerRegistry;

    @Override
    public Task<T> createTask(Task<T> task) {
        return taskRepository.save(task);
    }

    @Override
    public Task<T> saveTask(Task<T> task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task<T> task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    @Override
    public List<Task<T>> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task<T> getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void completeTask(Long taskId) {
        Task<T> task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));

        // mark the task as completed
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(new Date());
        taskRepository.save(task);

        // get the related entity type and call the completion handler
        Class<T> entityType = (Class<T>) ClassUtils.forName(task.getRelatedEntityType(), ClassUtils.getDefaultClassLoader());
        TaskCompletionHandler<T> handler = handlerRegistry.getHandler(entityType);
        if (handler != null) {
            handler.handleCompletion(task);
        }
    }
}

