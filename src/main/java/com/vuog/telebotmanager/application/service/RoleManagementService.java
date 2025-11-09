package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.entity.UserAccess;
import com.vuog.telebotmanager.domain.repository.UserPermissionRepository;
import com.vuog.telebotmanager.domain.valueobject.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleManagementService {

    private final UserPermissionRepository userPermissionRepository;

    @Transactional
    public UserAccess setRole(String userId, Long botId, UserRole role) {
        UserAccess perm = findOrCreate(userId, botId);
        perm.setRole(role);
        return userPermissionRepository.save(perm);
    }

    @Transactional(readOnly = true)
    public UserRole getRole(String userId, Long botId) {
        return userPermissionRepository.findFirstByUserIdAndBotId(userId, botId)
                .orElseGet(() -> userPermissionRepository.findFirstByUserIdAndBotIdIsNull(userId).orElse(null))
                .getRole();
    }

    @Transactional
    public UserAccess allowCommands(String userId, Long botId, String csvCommands, boolean add) {
        UserAccess perm = findOrCreate(userId, botId);
        Set<String> current = parseCsv(perm.getAllowedCommands());
        Set<String> update = parseCsv(csvCommands);
        if (add) {
            current.addAll(update);
        } else {
            current.removeAll(update);
        }
        perm.setAllowedCommands(String.join(",", current));
        return userPermissionRepository.save(perm);
    }

    @Transactional(readOnly = true)
    public String listAllowedCommands(String userId, Long botId) {
        UserAccess perm = userPermissionRepository.findFirstByUserIdAndBotId(userId, botId)
                .orElseGet(() -> userPermissionRepository.findFirstByUserIdAndBotIdIsNull(userId).orElse(null));
        if (perm == null || perm.getAllowedCommands() == null) return "";
        return perm.getAllowedCommands();
    }

    private UserAccess findOrCreate(String userId, Long botId) {
        return userPermissionRepository.findFirstByUserIdAndBotId(userId, botId)
                .orElseGet(() -> {
                    UserAccess p = UserAccess.builder()
                            .userId(userId)
                            .botId(botId)
                            .role(UserRole.ANONYMOUS)
                            .allowedCommands("")
                            .build();
                    return userPermissionRepository.save(p);
                });
    }

    private Set<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return new LinkedHashSet<>();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
