package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.Bot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Bot entity
 * Follows Clean Architecture by defining repository contracts in domain layer
 */
public interface BotRepository extends BaseRepository<Bot, Long>, BaseQueryRepository<Bot, Long> {

    /**
     * Find bot by token
     */
    Optional<Bot> findByBotToken(String botToken);

    /**
     * Find bot by username
     */
    Optional<Bot> findByBotUsername(String botUsername);

    /**
     * Find active bots
     */
    List<Bot> findByIsActiveTrue();

    /**
     * Find bots by status
     */
    List<Bot> findByStatus(Bot.BotStatus status);

    /**
     * Find bots by status and active flag
     */
    List<Bot> findByStatusAndIsActive(Bot.BotStatus status, Boolean isActive);

    /**
     * Find bots created by user
     */
    Page<Bot> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * Find bots with pagination
     */
    Page<Bot> findAll(Pageable pageable);

    /**
     * Check if bot token exists
     */
    boolean existsByBotToken(String botToken);

    /**
     * Check if bot username exists
     */
    boolean existsByBotUsername(String botUsername);

    /**
     * Find operational bots (active and status = ACTIVE)
     */
    List<Bot> findOperationalBots();

    /**
     * Find bots by status with custom query
     */
    List<Bot> findByStatusAndActive(Bot.BotStatus status, Boolean isActive);

    /**
     * Count bots by status
     */
    long countByStatus(Bot.BotStatus status);

    /**
     * Find bots with webhook URLs
     */
    List<Bot> findBotsWithWebhooks();
}
