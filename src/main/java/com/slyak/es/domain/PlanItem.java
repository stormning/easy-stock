package com.slyak.es.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
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

    private long realAmount;

    private BigDecimal cost;

    private BigDecimal price;

    private PlanItemStatus status = PlanItemStatus.WAIT;

    @Column(length = 100)
    private String remark;


    @Transient
    public PlanItem copy() {
        PlanItem planItem = new PlanItem();
        BeanUtils.copyProperties(this, planItem);
        return planItem;
    }
    @Transient
    public void subtract(long subtractAmount) {
        this.realAmount = this.realAmount - subtractAmount;
        this.cost = this.cost.subtract(price.multiply(BigDecimal.valueOf(subtractAmount)));
    }

    @Transient
    public void add(long addAmount) {
        this.realAmount = this.realAmount + addAmount;
        this.cost = this.cost.add(price.multiply(BigDecimal.valueOf(addAmount)));
    }
}
