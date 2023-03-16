package com.slyak.es.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JpaUtil {

    public static <T> Specification<T> getQuerySpecification(T entity, Sort sort) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Field> fields = getAllInstanceFields(entity.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    if (value != null && !value.toString().equals("0")) {
                        if (value instanceof String) {
                            if (!((String) value).isEmpty()) {
                                predicates.add(cb.like(root.get(field.getName()), "%" + value + "%"));
                            }
                        } else if (field.isAnnotationPresent(ManyToOne.class) && field.getType().isAnnotationPresent(Entity.class)) {
                            Join<?, ?> join = root.join(field.getName(), JoinType.LEFT);
                            predicates.add(cb.equal(join, value));

                        } else {
                            predicates.add(cb.equal(root.get(field.getName()), value));
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.error("getQuerySpecification error:", e);
                }
            }
            if (sort != null) {
                List<Order> orders = new ArrayList<>();
                for (Sort.Order order : sort) {
                    String property = order.getProperty();
                    if (order.isDescending()) {
                        orders.add(cb.desc(root.get(property)));
                    } else {
                        orders.add(cb.asc(root.get(property)));
                    }
                }
                query.orderBy(orders);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static <T> List<Field> getAllInstanceFields(Class<T> clazz) {
        return FieldUtils.getAllFieldsList(clazz)
                .stream()
                .filter(field -> !java.lang.reflect.Modifier.isStatic(field.getModifiers()))
                .collect(Collectors.toList());
    }
}
