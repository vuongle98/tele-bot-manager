package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.entity.UserAccess;
import com.vuog.telebotmanager.domain.repository.UserPermissionRepository;
import com.vuog.telebotmanager.domain.valueobject.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public boolean canCreateCommand(Long botId) {
        String userId = currentUserId(); // Keycloak ID if present
        UserRole role = getEffectiveRole(userId, botId);
        return role != null && role.atLeast(UserRole.MODERATOR);
    }

    public boolean canCreatePlugin(Long botId) {
        String userId = currentUserId(); // Keycloak ID if present
        UserRole role = getEffectiveRole(userId, botId);
        return role != null && role.atLeast(UserRole.MODERATOR);
    }

    public List<String> filterAllowedCommands(String userId, Long botId, List<String> commands) {
        UserAccess perm = findEffectivePermission(userId, botId);
        if (perm == null) return Collections.emptyList();
        if (perm.getRole() != null && perm.getRole().atLeast(UserRole.MODERATOR)) return commands;
        if (perm.getAllowedCommands() == null || perm.getAllowedCommands().isBlank()) return Collections.emptyList();
        Set<String> allowed = Arrays.stream(perm.getAllowedCommands().split(","))
                .map(String::trim)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        return commands.stream()
                .filter(c -> allowed.contains(c.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    private UserAccess findEffectivePermission(String userId, Long botId) {
        if (userId == null) return null;
        // Try Telegram mapping first
        if (botId != null) {
            return userPermissionRepository.findFirstByTelegramUserIdAndBotId(userId, botId)
                    .orElseGet(() -> userPermissionRepository.findFirstByTelegramUserIdAndBotIdIsNull(userId)
                            .orElseGet(() -> userPermissionRepository.findFirstByUserIdAndBotId(userId, botId)
                                    .orElseGet(() -> userPermissionRepository.findFirstByUserIdAndBotIdIsNull(userId).orElse(null))));
        }
        return userPermissionRepository.findFirstByTelegramUserIdAndBotIdIsNull(userId)
                .orElseGet(() -> userPermissionRepository.findFirstByUserIdAndBotIdIsNull(userId).orElse(null));
    }

    public UserRole getEffectiveRole(String userId, Long botId) {
        UserAccess perm = findEffectivePermission(userId, botId);
        return perm != null ? perm.getRole() : UserRole.ANONYMOUS;
    }

    public boolean hasRoleAtLeast(Long botId, UserRole role) {
        String userId = currentUserId(); // Keycloak subject if present
        UserRole effective = getEffectiveRole(userId, botId);
        return effective != null && effective.atLeast(role);
    }

    // Overload for Telegram flow: pass telegramUserId explicitly
    public boolean hasRoleAtLeast(String telegramUserId, Long botId, UserRole role) {
        UserRole effective = getEffectiveRole(telegramUserId, botId);
        return effective != null && effective.atLeast(role);
    }

    public UserAccess registerIfAbsent(String telegramUserId, Long botId) {
        if (telegramUserId == null || telegramUserId.isBlank()) return null;
        UserAccess existing = findEffectivePermission(telegramUserId, botId);
        if (existing != null) return existing;
        UserAccess p = UserAccess.builder()
                .telegramUserId(telegramUserId)
                .userId(null)
                .realm(null)
                .botId(botId)
                .role(UserRole.ANONYMOUS)
                .allowedCommands("")
                .build();
        return userPermissionRepository.save(p);
    }

    public String currentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return null;
    }
}
