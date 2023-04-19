package com.slyak.es.service;

import com.alibaba.fastjson.JSON;
import com.slyak.es.domain.TradeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class TradeTaskContent implements TaskContentMaker<TradeTaskContent> {

    private TradeType tradeType;

    private BigDecimal price;

    private long amount;

    private String stock;

    private Long planId;


    public TradeTaskContent(String stock, TradeType tradeType, BigDecimal price, long amount) {
        this.tradeType = tradeType;
        this.price = price;
        this.amount = amount;
        this.stock = stock;
    }

    @Override
    public TradeTaskContent deserialize(String content) {
        return JSON.parseObject(content, this.getClass());
    }

    @Override
    public String serialize() {
        return JSON.toJSONString(this);
    }

    @Override
    public String getTitle() {
        return "【" + stock + "】" + "单价：" + price.toString() + "，" + tradeTypeTitle(tradeType) + "：" + amount + "股";
    }

    private String tradeTypeTitle(TradeType tradeType){
        return "<b style='color:" + (tradeType == TradeType.BUY ? "blue" : "red") + "'>" + tradeType.getTitle() + "</b>";
    }
}
