package com.slyak.es.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
//投资计划
@Table(name = "t_plan")
@Entity
public class Plan extends AbstractAuditable<User, Long> {

    //股票编号
    private String stockCode;

    //第一次买入价格
    private BigDecimal firstPrice;

    //最近一次买入价格
    private BigDecimal lastPrice;

    //心理预期最糟糕的价格
    private BigDecimal worstPrice;

    //预期投入本金
    private BigDecimal capital;

    //当前持股股数
    private long amount = 0;

    //当前总金额
    private BigDecimal turnover = BigDecimal.ZERO;

    //止赢参数
    private BigDecimal winPercent;

    //止损参数，同样也是补仓依据
    private BigDecimal losePercent;

    //补仓步长
    public BigDecimal getMarginStep() {
        int scale = Math.max(2, firstPrice.scale());
        return firstPrice.subtract(worstPrice).multiply(losePercent).divide(BigDecimal.valueOf(100), scale, RoundingMode.HALF_UP);
    }

    public BigDecimal getNextPrice() {
        return lastPrice.add(getMarginStep());
    }

}
