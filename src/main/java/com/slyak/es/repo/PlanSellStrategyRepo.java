package com.slyak.es.repo;

import com.slyak.es.domain.PlanItem;
import com.slyak.es.domain.PlanSellStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface PlanSellStrategyRepo extends JpaRepository<PlanSellStrategy, Long>, JpaSpecificationExecutor<PlanItem> {
    PlanSellStrategy findByPlanId(Long planId);
}