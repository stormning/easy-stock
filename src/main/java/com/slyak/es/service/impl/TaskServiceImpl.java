package com.slyak.es.service.impl;

import com.slyak.es.domain.Task;
import com.slyak.es.domain.TaskStatus;
import com.slyak.es.repo.TaskRepository;
import com.slyak.es.service.TaskContentMaker;
import com.slyak.es.service.TaskService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCompletionHandlerRegistry handlerRegistry;

    @Override
    public Task createTask(TaskContentMaker<?> contentMaker, Class<?> relClass, Long relId) {
        Task task = new Task();
        task.setContent(contentMaker.serialize());
        task.setTitle(contentMaker.getTitle());
        task.setRelatedEntityType(relClass.getName());
        task.setRelatedEntityId(relId);
        return saveTask(task);
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));

        // mark the task as completed
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(new Date());
        taskRepository.save(task);

        // get the related entity type and call the completion handler
        Class<?> entityType = ClassUtils.forName(task.getRelatedEntityType(), ClassUtils.getDefaultClassLoader());
        TaskCompletionHandler handler = handlerRegistry.getHandler(entityType);
        if (handler != null) {
            handler.handleCompletion(task);
        }
    }

    @Override
    @Transactional
    public void clearAll() {
        taskRepository.deleteAll();
    }
}

