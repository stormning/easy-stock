package com.slyak.es.hibernate.assembler;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author stormning 2018/8/6
 * @since 1.3.0
 */
@Slf4j
public class EntityAssemblers {

    private static final int maxDepth = 2;

    private Set<Integer> levels = Sets.newHashSet();

    private EntityAssemblers() {
    }

    public static EntityAssemblers newInstance() {
        return new EntityAssemblers();
    }

    public void assemble(Object obj) {
        assemble(obj, 0);
    }

    @SuppressWarnings("unchecked")
    private void assemble(Object obj, int level) {
        if (obj instanceof Iterable) {
            assembleMany((Iterable) obj, level);
        } else if (obj instanceof Map) {
            assembleMany(((Map) obj).values(), level);
        } else if (isEntityObject(obj)) {
            assembleOne(obj, level);
        }
    }

    @SuppressWarnings("unchecked")
    private void assembleOne(Object obj, int level) {
        List<EntityAssembler> entityAssemblers = EntityAssemblerHolder.getEntityAssemblers(obj.getClass());
        if (!CollectionUtils.isEmpty(entityAssemblers)) {
            for (EntityAssembler assembler : entityAssemblers) {
                assembler.assemble(obj);
            }
            if (needLoop()) {
                assembleObjectFields(obj, ++level);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void assembleMany(Iterable iterable, int level) {
        if (!iterable.iterator().hasNext()) {
            return;
        }
        Object object = iterable.iterator().next();
        if (isEntityObject(object)) {
            int nextLevel = level + 1;
            List<EntityAssembler> entityAssemblers = EntityAssemblerHolder.getEntityAssemblers(object.getClass());
            if (!CollectionUtils.isEmpty(entityAssemblers) && needLoop()) {
                for (EntityAssembler assembler : entityAssemblers) {
                    assembler.massemble(iterable);
                    for (Object o : iterable) {
                        assembleObjectFields(o, nextLevel);
                    }
                }
            }
        }
    }

    private void assembleObjectFields(Object object, int level) {
        ReflectionUtils.doWithFields(object.getClass(), field -> {
            if (AnnotationUtils.getAnnotation(field, Transient.class) != null || AnnotationUtils.getAnnotation(field, org.springframework.data.annotation.Transient.class) != null) {
                ReflectionUtils.makeAccessible(field);
                Object fieldObj = ReflectionUtils.getField(field, object);
                if (fieldObj != null) {
                    assemble(fieldObj, level);
                }
            }
        });
    }

    private boolean needLoop() {
        return levels.size() < maxDepth;
    }

    private boolean isEntityObject(Object object) {
        Annotation[] annotations;
        return object != null && (
                ((annotations = object.getClass().getAnnotations()) != null && Arrays.stream(annotations).anyMatch(annotation -> {
                            Class<? extends Annotation> aClass = annotation.annotationType();
                            return aClass == Entity.class || aClass == Assemble.class;
                        }
                )));
    }
}
