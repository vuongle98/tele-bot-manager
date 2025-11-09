package com.vuog.telebotmanager.infrastructure.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            // Nếu dùng Keycloak token trực tiếp
            return Optional.ofNullable(jwt.getClaimAsString("preferred_username"));
        }

        // Nếu bạn đã ánh xạ sang `UserDetails`, ví dụ dùng Keycloak adapter
        return Optional.ofNullable(authentication.getName());
    }
}