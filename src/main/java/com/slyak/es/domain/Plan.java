package com.slyak.es.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
//投资计划
@Table(name = "t_plan")
@Entity
@Accessors(chain = true)
public class Plan extends AbstractAuditable<User, Long> {

    //股票编号
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id")
    private Stock stock;


    //持股数
    private long amount = 0;

    //持仓总成本
    private BigDecimal cost = BigDecimal.ZERO;

    //平均持仓成本
    public BigDecimal getAvgPrice() {
        if (amount > 0) {
            //最小一百股，所以精度减2
            return cost.divide(BigDecimal.valueOf(amount), cost.scale() - 2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getWin() {
        BigDecimal avgPrice = getAvgPrice();
        if (avgPrice.compareTo(BigDecimal.ZERO) > 0) {
            return stock.getInfo().getPrice().subtract(avgPrice).multiply(BigDecimal.valueOf(100)).divide(avgPrice, 2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    //第一次买入价格
    private BigDecimal firstPrice;

    //最近一次买入价格
    private BigDecimal lastPrice;

    //心理预期最糟糕的价格
    private BigDecimal worstPrice;

    //预期投入本金
    private BigDecimal capital;

    //当前总金额
    private BigDecimal turnover = BigDecimal.ZERO;

    //止赢参数
    private BigDecimal winPercent;

    //止损参数，同样也是补仓依据
    private BigDecimal losePercent;

    //补仓步长
    @JsonIgnore
    public BigDecimal getMarginStep() {
        int scale = Math.max(2, firstPrice.scale());
        return firstPrice.subtract(worstPrice).multiply(losePercent).divide(BigDecimal.valueOf(100), scale, RoundingMode.HALF_UP);
    }

    @JsonIgnore
    public BigDecimal getNextPrice() {
        return lastPrice.add(getMarginStep());
    }

}
