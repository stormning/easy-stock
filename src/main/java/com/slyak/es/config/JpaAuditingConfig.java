package com.slyak.es.config;

import com.slyak.es.config.security.UserDetailsImpl;
import com.slyak.es.domain.User;
import com.slyak.es.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@Configuration
public class JpaAuditingConfig {

    @Autowired
    private UserRepo userRepo;

    @Bean(name = "springSecurityAuditorAware")
    public AuditorAware<User> springSecurityAuditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
            return userRepo.findById(principal.getId());
        };
    }
}