package com.slyak.es.controller;

import com.slyak.es.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock")
public class StockApiController extends BaseController {

    private final StockService stockService;

    @Autowired
    public StockApiController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/list")
    public Result stocks(String keyword) {
        return ok(stockService.queryStocks(keyword));
    }
}
