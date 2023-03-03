package com.slyak.es.repo;

import com.slyak.es.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockRepo extends JpaRepository<Stock, Long>, JpaSpecificationExecutor {
    Stock findByCode(String stockCode);
}
