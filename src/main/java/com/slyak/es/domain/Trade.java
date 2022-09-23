package com.slyak.es.domain;

import com.slyak.es.constant.Constants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@Table(name = "t_trade")
//交易记录
@Entity
public class Trade extends AbstractAuditable<User, Long> {

    //股票代码
    private String stock;

    //计划编号
    private Long planId;

    //单价
    private BigDecimal price;

    //数量
    private long amount;

    //实际成交额
    private BigDecimal turnover;

    //交易类型
    @Enumerated(EnumType.STRING)
    private TradeType type;

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(amount));
    }

    public Trade init(Plan plan, BigDecimal commission, TradeType type) {
        Assert.notNull(price, "Price must be set");
        this.type = type;
        this.stock = plan.getStock();
        this.planId = plan.getId();
        this.turnover = Constants.trade(getTotalPrice(), commission, type);
        return this;
    }
}