package com.slyak.es.task;


import com.google.common.collect.Range;
import com.slyak.es.domain.*;
import com.slyak.es.service.PlanService;
import com.slyak.es.service.SellStrategy;
import com.slyak.es.service.TaskService;
import com.slyak.es.service.TradeTaskContent;
import com.slyak.es.service.impl.PlanServiceImpl;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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
    @SneakyThrows
    public void generateTasks() {
        taskService.clearAll();
        List<Plan> plans = planService.queryUserPlans(null, null);
        for (Plan plan : plans) {
            Long id = plan.getId();
            Optional<User> createdBy = plan.getCreatedBy();
            Stock stock = plan.getStock();
            BigDecimal fixedPrice = stock.getInfo().getFixedPrice();
            String code = stock.getCode();
            int maxPercent = is20Percent(code) ? 20 : 10;

            BigDecimal low = mul(fixedPrice, -maxPercent);
            Range<BigDecimal> buyRange = Range.closed(low, BigDecimal.valueOf(Integer.MAX_VALUE));
            List<PlanItem> planItems = planService.getPlanItems(id);

            BigDecimal lowestItemPrice = BigDecimal.ZERO;
            for (PlanItem planItem : planItems) {
                BigDecimal price = planItem.getPrice();
                PlanItemStatus status = planItem.getStatus();
                //buy
                if (PlanItemStatus.BUY_STATUS.contains(status) && buyRange.contains(price)) {
                    long amountGap = planItem.getAmount() - planItem.getRealAmount();
                    if (amountGap > 0) {
                        TradeTaskContent ttc = new TradeTaskContent(stock.getName(), TradeType.BUY, price, amountGap);
                        taskService.createTask(ttc, PlanItem.class, planItem.getId(), createdBy.orElse(null));
                    }
                }
            }

            //min(plan avgPrice, last finished or part_finished item's price)
            //sell 提醒
            SellStrategy sellStrategy = planService.getSellStrategy(plan.getId());
            if (sellStrategy != null) {
                List<PriceAndAmount> pps = sellStrategy.generate();
                for (PriceAndAmount pp : pps) {
                    TradeTaskContent ttc = new TradeTaskContent(stock.getName(), TradeType.SELL, pp.getSellPrice(), pp.getSellAmount());
                    taskService.createTask(ttc, Plan.class, plan.getId(), createdBy.orElse(null));
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
