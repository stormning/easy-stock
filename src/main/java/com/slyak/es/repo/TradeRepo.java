package com.slyak.es.repo;

import com.slyak.es.domain.Trade;
import com.slyak.es.domain.TradeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepo extends JpaRepository<Trade, Long> {
    List<Trade> findByPlanIdAndType(Long planId, TradeType type, Sort sort);

    List<Trade> findByPlanId(Long planId, Sort sort);
}
