// Create this file in domain/bot/repository
package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot.BotStatus;
import com.vuog.telebotmanager.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramBotRepository extends JpaRepository<TelegramBot, Long> {
    
    // Find by API token (secured)
    Optional<TelegramBot> findByApiToken(String apiToken);
    
    // Find all bots by owner
    List<TelegramBot> findByOwner(User owner);
    
    // Find all bots by status
    List<TelegramBot> findByStatus(BotStatus status);
    
    // Find by name and owner (for uniqueness check)
    Optional<TelegramBot> findByNameAndOwner(String name, User owner);
    
    // Update status directly
    @Modifying
    @Query("UPDATE TelegramBot b SET b.status = :status WHERE b.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") BotStatus status);
    
    // Count bots by status
    long countByStatus(BotStatus status);
    
    // Find all running bots
    @Query("SELECT b FROM TelegramBot b WHERE b.status IN :runningStatuses")
    List<TelegramBot> findAllRunningBots(
        @Param("runningStatuses") List<BotStatus> runningStatuses
    );
    
    // Check if bot exists and is owned by user
    boolean existsByIdAndOwner(Long id, User owner);
}