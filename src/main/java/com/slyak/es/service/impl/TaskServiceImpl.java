package com.slyak.es.service.impl;

import com.slyak.es.config.security.SecurityUtils;
import com.slyak.es.domain.Task;
import com.slyak.es.domain.TaskStatus;
import com.slyak.es.domain.User;
import com.slyak.es.repo.TaskRepository;
import com.slyak.es.service.TaskContentMaker;
import com.slyak.es.service.TaskService;
import com.slyak.es.util.JpaUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskCompletionHandlerRegistry handlerRegistry;

    @Override
    @Transactional
    public Task createTask(TaskContentMaker<?> contentMaker, Class<?> relClass, Long relId, User user) {
        Task task = new Task();
        task.setContent(contentMaker.serialize());
        task.setTitle(contentMaker.getTitle());
        task.setRelatedEntityType(relClass.getName());
        task.setRelatedEntityId(relId);
        task.setCreatedBy(user);
        return saveTask(task);
    }

    @Override
    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    @Override
    public List<Task> getUserTasks(TaskStatus status) {
        Task task = new Task();
        User user = SecurityUtils.getUser();
        assert user != null;
        task.setCreatedBy(user);
        task.setStatus(status);
        return taskRepository.findAll(JpaUtil.getQuerySpecification(task, null));
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void completeTask(Long taskId) {
        Task task = getTaskById(taskId);

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

    @Override
    @Transactional
    public void startTask(Long taskId) {
        Task task = getTaskById(taskId);
        // mark the task as in progress
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);
    }
}

