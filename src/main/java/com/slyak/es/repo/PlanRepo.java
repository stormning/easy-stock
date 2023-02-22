package com.slyak.es.repo;

import com.slyak.es.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepo extends JpaRepository<Plan, Long> {
    Plan findByStockCode(String stockCode);
}
