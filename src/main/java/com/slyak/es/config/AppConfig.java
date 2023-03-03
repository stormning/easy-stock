package com.slyak.es.config;

import com.slyak.es.hibernate.assembler.EntityAssembler;
import com.slyak.es.hibernate.assembler.EntityAssemblerHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppConfig {
    private final List<EntityAssembler> entityAssemblers;

    @Autowired
    public AppConfig(List<EntityAssembler> entityAssemblers) {
        this.entityAssemblers = entityAssemblers;
    }

    @Bean
    public EntityAssemblerHolder entityAssemblerHolder() {
        return new EntityAssemblerHolder(entityAssemblers);
    }
}
