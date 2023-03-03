package com.slyak.es.service.impl;

import com.slyak.es.domain.Stock;
import com.slyak.es.domain.StockInfo;
import com.slyak.es.hibernate.assembler.EntityAssemblerOne;
import com.slyak.es.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class StockInfoAssembler extends EntityAssemblerOne<Stock, String, StockInfo> {

    private final StockService stockService;

    @Autowired
    public StockInfoAssembler(StockService stockService) {
        this.stockService = stockService;
    }


    @Override
    protected String getKey(Stock bean) {
        return bean.getMarket() + bean.getCode();
    }

    @Override
    protected void setValue(Stock bean, StockInfo value) {
        bean.setInfo(value);
    }

    @Override
    protected StockInfo getValue(String key) {
        return stockService.getStockInfo(key);
    }

    @Override
    protected Map<String, StockInfo> mgetValue(Collection<String> keys) {
        return stockService.mgetStockInfos(keys);
    }
}
