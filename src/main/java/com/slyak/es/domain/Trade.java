package com.slyak.es.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "t_trade")
//交易记录
public class Trade extends AbstractAuditable<User, Long> {

    //股票代码
    private String stock;

    //计划编号
    private Long planId;

    //价格
    private BigDecimal price;

    //数量
    private long amount;

    //交易金额
    private BigDecimal money;

    //交易类型
    @Enumerated(EnumType.STRING)
    private TradeType type;
}