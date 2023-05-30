package com.slyak.es.controller;

import com.slyak.es.config.security.SecurityUtils;
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

    @GetMapping("/get")
    public Result plan(Long id) {
        return ok(planService.getById(id));
    }

    @GetMapping("/list")
    public Result plans(String stockCode) {
        Stock stock = null;
        if (StringUtils.hasText(stockCode)) {
            stock = stockService.getStock(stockCode);
        }
        return ok(planService.queryUserPlans(SecurityUtils.getUser(), stock));
    }

    @GetMapping("/items")
    public Result items(Long id) {
        return ok(planService.getPlanItems(id));
    }

    @PostMapping("/genItemsByStrategy")
    public Result genItemsByStrategy(@RequestBody StrategyRequest request, Long id) {
        request.setPlanId(id);
        PriceStrategy ss = request.initStrategy();
        BigDecimal startCost = request.getStartCost();
        BigDecimal supplement = request.getSupplement();
        List<PlanItem> items = planService.genPlanItems(id, ss.generate(), startCost, supplement);
        return ok(items);
    }

    @PostMapping("/saveItem")
    public Result saveItem(@RequestBody PlanItem planItem){
        return ok(planService.savePlanItem(planItem));
    }

    @PostMapping("/deleteItem")
    public Result deleteItem(Long id) {
        planService.deletePlanItem(id);
        return ok();
    }

    @PostMapping("/deleteItems")
    public Result deleteItems(Long id) {
        planService.deletePlanItems(id);
        return ok();
    }

    @PostMapping("/finishItem")
    public Result finishItem(Long id, Long amount) {
        planService.finishItem(id, amount);
        return ok();
    }
}
