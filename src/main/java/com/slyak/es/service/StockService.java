package com.slyak.es.service;

import com.slyak.es.domain.Plan;
import com.slyak.es.domain.Stock;

import java.util.List;

public interface StockService {
    List<Stock> queryStocks(String keyword);

    Stock getStock(String stockCode);
}
