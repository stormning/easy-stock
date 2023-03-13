package com.slyak.es.controller;

import com.slyak.es.domain.Plan;
import com.slyak.es.domain.PlanItem;
import com.slyak.es.domain.Stock;
import com.slyak.es.service.PlanService;
import com.slyak.es.service.PriceStrategy;
import com.slyak.es.service.StockService;
import com.slyak.es.service.StrategyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/plan")
@Slf4j
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
        log.info("init .....................");
        planService.init(stockCode);
        return ok();
    }

    @PostMapping("/save")
    public Result save(@ModelAttribute Plan plan) {
        return ok();
    }

    @GetMapping("/get")
    public Result plan(@ModelAttribute Plan plan) {
        return ok(plan);
    }

    @GetMapping("/list")
    public Result plans(String stockCode) {
        Stock stock = null;
        if (StringUtils.hasText(stockCode)) {
            stock = stockService.getStock(stockCode);
        }
        return ok(planService.queryPlans(stock));
    }

    @GetMapping("/items")
    public Result items(@ModelAttribute Plan plan) {
        return ok(planService.getPlanItems(plan.getId()));
    }

    @PostMapping("/genItemsByStrategy")
    public Result genItemsByStrategy(@RequestBody StrategyRequest request, @ModelAttribute Plan plan) {
        PriceStrategy ss = request.initStrategy();
        BigDecimal startCost = request.getStartCost();
        BigDecimal supplement = request.getSupplement();
        List<PlanItem> items = planService.genPlanItems(plan.getId(), ss.genPriceSteps(), startCost, supplement);
        return ok(items);
    }

    @PostMapping("/savePlanItems")
    public Result savePlanItems(@RequestBody List<PlanItem> items, @ModelAttribute Plan plan) {
        items.forEach(planItem -> planItem.setPlanId(plan.getId()));
        planService.savePlanItems(items);
        return ok();
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
