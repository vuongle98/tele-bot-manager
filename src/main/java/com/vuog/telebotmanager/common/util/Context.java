package com.vuog.telebotmanager.common.util;

import com.vuog.telebotmanager.common.dto.UserResponseDto;
import com.vuog.telebotmanager.common.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class Context {

    private static final ThreadLocal<UserResponseDto> currentUser = new ThreadLocal<>();

    public static UserResponseDto getUser() {
        // Check if the current user is set in the ThreadLocal (for seeding)
        UserResponseDto user = currentUser.get();

        if (user != null) {
            return user;
        }

        // Fall back to SecurityContextHolder if no thread-local user is set
        if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            throw new UserNotFoundException("User not authenticated");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserResponseDto)) {
            throw new UserNotFoundException("User not authenticated");
        }

        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println("Username" + authentication.getName());

        return (UserResponseDto) authentication.getPrincipal();
    }

    // Set the current user for seeding or other special operations
    public static void setUser(UserResponseDto user) {
        currentUser.set(user);
    }

    public static void setSystemUser() {
        UserResponseDto systemUser = new UserResponseDto("system", "system@example.com");
        Context.setUser(systemUser);  // Set the default user for audit logs
    }

    // Clear the user after seeding or operation
    public static void clear() {
        currentUser.remove();
        SecurityContextHolder.clearContext();
    }
}
