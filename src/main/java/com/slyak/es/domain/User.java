package com.slyak.es.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.AbstractPersistable;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends AbstractPersistable<Long> {
    private String name;
    private String pwd;
}
