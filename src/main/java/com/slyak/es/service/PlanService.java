package com.slyak.es.service;

import com.slyak.es.domain.Plan;
import com.slyak.es.domain.Stock;

import java.util.List;

public interface PlanService {
    Plan getById(Long id);

    Plan init(String stockCode);

    List<Plan> queryPlans(Stock stock);
}
