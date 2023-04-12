package com.slyak.es.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceAndAmount {

    private BigDecimal sellPrice;

    private long sellAmount;
}
