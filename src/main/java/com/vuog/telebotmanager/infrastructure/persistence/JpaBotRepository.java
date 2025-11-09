package com.vuog.telebotmanager.infrastructure.persistence;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaBotRepository extends JpaRepository<Bot, Long>, BotRepository {
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
    @Query("SELECT b FROM Bot b WHERE b.isActive = true AND b.status = 'ACTIVE'")
    List<Bot> findOperationalBots();

    /**
     * Find bots by status with custom query
     */
    @Query("SELECT b FROM Bot b WHERE b.status = :status AND b.isActive = :isActive")
    List<Bot> findByStatusAndActive(@Param("status") Bot.BotStatus status,
                                    @Param("isActive") Boolean isActive);

    /**
     * Count bots by status
     */
    @Query("SELECT COUNT(b) FROM Bot b WHERE b.status = :status")
    long countByStatus(@Param("status") Bot.BotStatus status);

    /**
     * Find bots with webhook URLs
     */
    @Query("SELECT b FROM Bot b WHERE b.webhookUrl IS NOT NULL AND b.webhookUrl != ''")
    List<Bot> findBotsWithWebhooks();
}
