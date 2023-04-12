package com.slyak.es.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@Table(name = "t_stock")
//交易记录
@Entity
@NoArgsConstructor
public class Stock extends AbstractPersistable<Long> {
    private String name;
    private String code;
    private Market market;
    private String pinyin;
    private OrgType orgType;
    private StockType type;

    @Transient
    private StockInfo info;


    @Transient
    public BigDecimal getMaxPercent(){
        return code.startsWith("3") ? BigDecimal.valueOf(20) : BigDecimal.valueOf(10);
    }
}
