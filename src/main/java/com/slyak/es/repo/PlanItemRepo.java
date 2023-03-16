package com.slyak.es.repo;

import com.slyak.es.domain.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface PlanItemRepo extends JpaRepository<PlanItem, Long>, JpaSpecificationExecutor<PlanItem> {

    List<PlanItem> findPlanItemsByPlanIdOrderByPriceDesc(Long id);

    @Modifying
    void deleteByPlanId(Long planId);
}