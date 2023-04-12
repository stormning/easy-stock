package com.slyak.es.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
//投资计划
@Table(name = "t_plan")
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Plan extends AbstractAuditable<User, Long> {

    //股票编号
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    //持股数
    private long amount = 0;

    //持仓总成本
    private BigDecimal cost = BigDecimal.ZERO;


    //市值
    public BigDecimal getMarketVal(){
        return __getMarketVal().setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal __getMarketVal(){
        return stock.getInfo().getPrice().multiply(BigDecimal.valueOf(amount));
    }

    //平均持仓成本
    public BigDecimal getAvgPrice() {
        if (amount > 0) {
            //最小一百股，所以精度减2
            return cost.divide(BigDecimal.valueOf(amount), cost.scale(), RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    //盈亏百分比
    public BigDecimal getWinPercent() {
        if (cost.compareTo(BigDecimal.ZERO) > 0) {
            return getWin().multiply(BigDecimal.valueOf(100)).divide(cost, 2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    //盈亏总额
    public BigDecimal getWin(){
        return __getMarketVal().subtract(cost).setScale(0, RoundingMode.HALF_UP);
    }
}
