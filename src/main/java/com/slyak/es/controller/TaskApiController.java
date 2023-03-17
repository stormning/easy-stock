package com.slyak.es.controller;

import com.slyak.es.domain.Task;
import com.slyak.es.domain.TaskStatus;
import com.slyak.es.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@Slf4j
public class TaskApiController<T extends Persistable<Long>> extends BaseController {
    @Autowired
    private TaskService<T> taskService;

    @GetMapping("/list")
    public List<Task<T>> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping("/complete")
    public Result complete(Long id) {
        taskService.completeTask(id);
        return ok();
    }

    @PostMapping("/delete")
    public Result delete(Long id) {
        taskService.deleteTask(id);
        return ok();
    }
}