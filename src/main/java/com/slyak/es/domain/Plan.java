package com.slyak.es.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.Table;
import java.math.BigDecimal;

@Data
//投资计划
@Table(name = "t_plan")
public class Plan extends AbstractAuditable<User, Long> {

    //股票编号
    private String stock;

    //第一次买入价格
    private BigDecimal firstPrice;

    //心理预期最糟糕的价格
    private BigDecimal worstPrice;

    //补仓跌幅
    private BigDecimal marginCall;

    //预期投入本金
    private BigDecimal capital;
}
