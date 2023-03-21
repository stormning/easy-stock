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
        List<Plan> plans = planService.queryUserPlans(null, null);
        for (Plan plan : plans) {
            Long id = plan.getId();
            Stock stock = plan.getStock();
            BigDecimal fixedPrice = stock.getInfo().getFixedPrice();
            String code = stock.getCode();
            int maxPercent = is20Percent(code) ? 20 : 10;

            BigDecimal high = mul(fixedPrice, maxPercent);
            BigDecimal low = mul(fixedPrice, -maxPercent);

            Range<BigDecimal> range = Range.closed(low, high);

            List<PlanItem> planItems = planService.getPlanItems(id);
            for (PlanItem planItem : planItems) {
                BigDecimal price = planItem.getPrice();

                //buy
                if (planItem.getStatus() == PlanItemStatus.WAIT && range.contains(price)) {
                    PlanServiceImpl.PlanItemTaskContent pitc = new PlanServiceImpl.PlanItemTaskContent(TradeType.BUY, price, planItem.getAmount());
                    taskService.createTask(pitc, PlanItem.class, planItem.getId());
                }

                if (planItem.getStatus() == PlanItemStatus.FINISH && range.contains(price)) {
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
