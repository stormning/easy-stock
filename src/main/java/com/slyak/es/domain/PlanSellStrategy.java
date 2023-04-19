package com.slyak.es.domain;

import com.google.common.collect.Maps;
import com.slyak.es.service.JsonConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Map;

@Data
//投资计划
@Table(name = "t_plan_sell_strategy")
@Entity
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class PlanSellStrategy extends AbstractAuditable<User, Long> {
    @Column
    private long planId;

    @Column
    private String className;

    @Convert(converter = JsonConverter.class)
    private Map<String, String> params = Maps.newHashMap();
}
