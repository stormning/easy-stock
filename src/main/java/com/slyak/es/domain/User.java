package com.slyak.es.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_user")
@NoArgsConstructor
public class User extends AbstractPersistable<Long> {
    private String name;

    @JsonIgnore
    private String pwd;
    //手续费率
    private BigDecimal commission;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "t_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
