package com.slyak.es.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class User extends AbstractPersistable<Long> {
    private String name;
    private String pwd;
    //手续费率
    private BigDecimal commission;
}
