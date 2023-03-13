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


    private static final BigDecimal MIN_FEE = BigDecimal.valueOf(5);

    /**
     * 计算交易金额
     *
     * @param totalCost 总价值
     * @param commission 佣金比率
     * @param type       交易类型
     * @return
     */
    public static BigDecimal trade(BigDecimal totalCost, BigDecimal commission, TradeType type) {
        BigDecimal finalPrice;
        BigDecimal com = minFee(totalCost.multiply(commission));
        if (type == TradeType.BUY) {
            BigDecimal tradeFee = com.add(totalCost.multiply(TRADE_TAX));
            finalPrice = totalCost.add(tradeFee);
        } else {
            BigDecimal tradeFee = com.add(totalCost.multiply(TAX_YIN_HUA.add(TRADE_TAX)));
            finalPrice = totalCost.subtract(tradeFee);
        }
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal minFee(BigDecimal fee) {
        return fee.compareTo(MIN_FEE) >= 0 ? fee : MIN_FEE;
    }

    public static void main(String[] args) {
        System.out.println(trade(BigDecimal.valueOf(20000), BigDecimal.valueOf(0.0001d), TradeType.BUY));
        System.out.println(trade(BigDecimal.valueOf(20000), BigDecimal.valueOf(0.0001d), TradeType.SELL));
    }
}
