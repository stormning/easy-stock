package com.slyak.es.service;

import com.slyak.es.domain.*;

import java.math.BigDecimal;
import java.util.List;

public interface PlanService {
    Plan getById(Long id);

    Plan init(String stockCode);

    List<Plan> queryUserPlans(User user, Stock stock);

    List<PlanItem> getPlanItems(Long planId);

    /**
     * 生成计划项
     * @param planId 计划编号
     * @param prices 价格表
     * @param startCost 起手最大金额
     * @param supplement 补仓至亏损幅度
     * @return 计划项
     */
    List<PlanItem> genPlanItems(Long planId, List<BigDecimal> prices, BigDecimal startCost, BigDecimal supplement);

    void savePlanItems(List<PlanItem> items);

    PlanItem savePlanItem(PlanItem planItem);

    void deletePlanItem(Long id);

    void finishItem(Long id);

    void deletePlanItems(Long id);

    void sellPlanItem(Long planId, long amount);

    SellStrategy getSellStrategy(Long planId);

    void finishItem(Long id, Long amount);
}
