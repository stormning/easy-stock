package com.slyak.es.constant;

import com.slyak.es.domain.TradeType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Constants {
    //印花税率，买入股票免征印花税，卖出收
    public static final BigDecimal TAX_YIN_HUA = BigDecimal.valueOf(0.001d);

    //过户费率，双边收取
    public static final BigDecimal TRADE_TAX = BigDecimal.valueOf(0.00002d);

    //券商佣金（每个人不同）

    /**
     * 计算交易金额
     *
     * @param totalPrice 总价值
     * @param commission 佣金比率
     * @param type       交易类型
     * @return
     */
    private static BigDecimal trade(BigDecimal totalPrice, BigDecimal commission, TradeType type) {
        BigDecimal finalPrice;
        if (type == TradeType.BUY) {
            finalPrice = BigDecimal.ZERO.subtract(totalPrice.add(totalPrice.multiply(TRADE_TAX)));
        } else {
            finalPrice = totalPrice.subtract(totalPrice.multiply(TAX_YIN_HUA.add(TRADE_TAX)));
        }
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        System.out.println(trade(BigDecimal.valueOf(20000), BigDecimal.valueOf(0.0001d), TradeType.BUY));
        System.out.println(trade(BigDecimal.valueOf(20000), BigDecimal.valueOf(0.0001d), TradeType.SELL));
    }
}
