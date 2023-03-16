package com.slyak.es.config;

import com.slyak.es.domain.User;
import com.slyak.es.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserRepo userRepo;

    @Autowired
    public SecurityUtils(UserRepo userRepo) {
        SecurityUtils.userRepo = userRepo;
    }

    public static User getUser() {
        Long userId = getUserId();
        if (userId == null) {
            return null;
        } else {
            return userRepo.findById(userId).orElse(null);
        }
    }

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getId();
    }

}
