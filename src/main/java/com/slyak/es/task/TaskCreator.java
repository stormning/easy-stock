package com.slyak.es.task;


import com.google.common.collect.Range;
import com.slyak.es.domain.*;
import com.slyak.es.service.PlanService;
import com.slyak.es.service.TaskService;
import com.slyak.es.service.impl.PlanServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class TaskCreator {

    private final PlanService planService;

    private final TaskService taskService;

    public TaskCreator(PlanService planService, TaskService taskService) {
        this.planService = planService;
        this.taskService = taskService;
    }

    @Scheduled(cron = "0 5 15 * * ?")
    @Transactional
    public void generateTasks() {
        taskService.clearAll();
        List<Plan> plans = planService.queryUserPlans(null, null);
        for (Plan plan : plans) {
            Long id = plan.getId();
            Stock stock = plan.getStock();
            BigDecimal fixedPrice = stock.getInfo().getFixedPrice();
            String code = stock.getCode();
            int maxPercent = is20Percent(code) ? 20 : 10;

            BigDecimal low = mul(fixedPrice, -maxPercent);
            BigDecimal high = mul(fixedPrice, maxPercent);

            Range<BigDecimal> buyRange = Range.closed(low, high);
            Range<BigDecimal> sellRange = Range.closed(fixedPrice, high);

            List<PlanItem> planItems = planService.getPlanItems(id);
            for (PlanItem planItem : planItems) {
                BigDecimal price = planItem.getPrice();

                //buy
                if (planItem.getStatus() == PlanItemStatus.WAIT && buyRange.contains(price)) {
                    PlanServiceImpl.PlanItemTaskContent pitc
                            = new PlanServiceImpl.PlanItemTaskContent(stock.getName(), TradeType.BUY, price, planItem.getAmount());
                    taskService.createTask(pitc, PlanItem.class, planItem.getId());
                }

                if (planItem.getStatus() == PlanItemStatus.FINISH && sellRange.contains(price)) {
                    //TODO sell 逻辑
                }
            }
        }

        System.out.println("generateTasks generateTasks generateTasks generateTasks");
    }

    private boolean is20Percent(String code) {
        return code.startsWith("3");
    }

    private BigDecimal mul(BigDecimal price, int percent) {
        return price.multiply(BigDecimal.valueOf(100 + percent)).divide(BigDecimal.valueOf(100), price.scale(), RoundingMode.FLOOR);
    }
}
