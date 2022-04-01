package com.slyak.es.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plan")
public class PlanApiController extends BaseController {

    @PostMapping("/create")
    public Result create() {
        return ok();
    }

    @GetMapping("/list")
    public Result list() {
        return ok();
    }
}
