package com.slyak.es.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

@Slf4j
public class JpaUtil {

    public static <T> Specification<T> getQuerySpecification(T entity) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    if (value != null && !value.toString().equals("0")) {
                        if (value instanceof String) {
                            if (!((String) value).isEmpty()) {
                                predicates.add(cb.like(root.get(field.getName()), "%" + value + "%"));
                            }
                        } else {
                            predicates.add(cb.equal(root.get(field.getName()), value));
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.error("getQuerySpecification error:", e);
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
