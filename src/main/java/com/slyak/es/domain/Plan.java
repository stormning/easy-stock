package com.slyak.es.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.math.BigDecimal;

@Data
public class Plan extends AbstractAuditable<User, Long> {

    private String stock;

    private BigDecimal buyPrice;

    private BigDecimal worstPrice;

    private BigDecimal marginCall;

    private BigDecimal capital;
}
