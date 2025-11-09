package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.UserAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserAccess, Long> {
    Optional<UserAccess> findFirstByUserIdAndBotId(String userId, Long botId);
    List<UserAccess> findByUserId(String userId);
    Optional<UserAccess> findFirstByUserIdAndBotIdIsNull(String userId);

    Optional<UserAccess> findFirstByTelegramUserIdAndBotId(String telegramUserId, Long botId);
    Optional<UserAccess> findFirstByTelegramUserIdAndBotIdIsNull(String telegramUserId);
}
