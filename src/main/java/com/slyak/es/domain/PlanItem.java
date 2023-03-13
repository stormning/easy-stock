package com.slyak.es.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
//投资计划
@Table(name = "t_plan_item")
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class PlanItem extends AbstractAuditable<User, Long> {

    private Long planId;

    private long amount;

    private BigDecimal price;

    private BigDecimal cost;

    private PlanItemStatus status = PlanItemStatus.WAIT;

    @Column(length = 100)
    private String remark;
}
