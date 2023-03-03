package com.slyak.es.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.*;

@Data
//投资计划
@Table(name = "t_plan_item")
@Entity
@Accessors(chain = true)
public class PlanItem extends AbstractAuditable<User, Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private long amount;

    private PlanItemStatus status;

    @Column(length = 100)
    private String remark;
}
