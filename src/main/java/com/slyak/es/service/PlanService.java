package com.slyak.es.service;

import com.slyak.es.domain.Plan;

public interface PlanService {
    Plan getById(Long id);

    Plan getByStockCode(String stockCode);

    Plan init(String stockCode);
}
