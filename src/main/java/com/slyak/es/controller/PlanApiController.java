package com.slyak.es.controller;

import com.slyak.es.domain.Plan;
import com.slyak.es.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
public class PlanApiController extends BaseController {

    private PlanService planService;

    @Autowired
    public PlanApiController(PlanService planService) {
        this.planService = planService;
    }


    @PostMapping("/save")
    public Result save(@ModelAttribute Plan plan) {
        return ok();
    }

    @GetMapping("/list")
    public Result list() {
        return ok();
    }

    @ModelAttribute
    public Plan getPlan(Long id) {
        Plan plan = planService.getById(id);
        return plan == null ? new Plan() : plan;
    }
}
