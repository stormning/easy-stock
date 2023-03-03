package com.slyak.es.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Accessors(chain = true)
@Table(name = "t_stock")
//交易记录
@Entity
public class Stock extends AbstractPersistable<Long> {
    private String name;
    private String code;
    private Market market;
    private String pinyin;
    private OrgType orgType;
    private StockType type;

    @Transient
    private StockInfo info;
}
