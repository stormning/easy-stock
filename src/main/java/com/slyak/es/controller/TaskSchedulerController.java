package com.slyak.es.controller;

import com.slyak.es.config.ScheduledTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler")
public class TaskSchedulerController extends BaseController{

    private final ScheduledTaskService scheduledTaskService;

    public TaskSchedulerController(ScheduledTaskService scheduledTaskService) {
        this.scheduledTaskService = scheduledTaskService;
    }


    @GetMapping("/tasks")
    public Result getAllTasks() {
        return ok(scheduledTaskService.getTaskInfos());
    }

    @PostMapping("/runTask")
    public Result runTask(String name) {
        scheduledTaskService.runTask(name);
        return ok();
    }
}

