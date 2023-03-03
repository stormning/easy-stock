package com.slyak.es.service;

import com.slyak.es.domain.Stock;
import com.slyak.es.domain.StockInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StockService {
    List<Stock> queryStocks(String keyword);

    Stock getStock(String stockCode);

    StockInfo getStockInfo(String stockCode);

    Map<String, StockInfo> mgetStockInfos(Collection<String> stockCodes);

    List<StockInfo> getStockInfos(Collection<String> stockCodes);
}
