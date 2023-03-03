package com.slyak.es.hibernate.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.OrderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * .
 *
 * @author stormning 2018/8/6
 * @since 1.3.0
 */
public class EntityAssemblerHolder{

    private static final Map<Class<?>, List<EntityAssembler>> ENTITY_ASSEMBLERS = new ConcurrentHashMap<>();

    @Autowired
    public EntityAssemblerHolder(List<EntityAssembler> entityAssemblers) {
        for (EntityAssembler ab : entityAssemblers) {
            Class p0 = GenericTypeResolver.resolveTypeArgument(ab.getClass(),EntityAssembler.class);
            List<EntityAssembler> ass = ENTITY_ASSEMBLERS.computeIfAbsent(p0, k -> new ArrayList<>());
            ass.add(ab);
        }
        for (List<EntityAssembler> ess : ENTITY_ASSEMBLERS.values()) {
            ess.sort((o1, o2) -> OrderUtils.getOrder(o2.getClass()) - OrderUtils.getOrder(o1.getClass()));
        }
    }

    public static List<EntityAssembler> getEntityAssemblers(Class<?> aClass) {
        return ENTITY_ASSEMBLERS.get(aClass);
    }
}
