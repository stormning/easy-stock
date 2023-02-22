package com.slyak.es.controller;

import com.slyak.es.domain.Plan;
import com.slyak.es.domain.Stock;
import com.slyak.es.service.PlanService;
import com.slyak.es.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
public class PlanApiController extends BaseController {

    private StockService stockService;

    private PlanService planService;

    @Autowired
    public PlanApiController(StockService stockService, PlanService planService) {
        this.stockService = stockService;
        this.planService = planService;
    }

    @PostMapping("/init")
    public Result init(String stockCode) {
        planService.init(stockCode);
        return ok();
    }


    @PostMapping("/save")
    public Result save(@ModelAttribute Plan plan) {
        return ok();
    }

    @GetMapping("/list")
    public Result stocks(String keyword) {
        return ok(stockService.queryStocks(keyword));
    }

    @ModelAttribute
    public Plan getPlan(Long id) {
        if (id == null) {
            return null;
        }
        Plan plan = planService.getById(id);
        return plan == null ? new Plan() : plan;
    }
}
