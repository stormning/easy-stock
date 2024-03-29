package com.slyak.es.repo;

import com.slyak.es.domain.Plan;
import com.slyak.es.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PlanRepo extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {
}