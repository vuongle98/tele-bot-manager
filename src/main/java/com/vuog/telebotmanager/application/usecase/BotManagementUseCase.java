package com.vuog.telebotmanager.application.usecase;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotHistory;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.request.CreateBotRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateBotRequest;
import com.vuog.telebotmanager.presentation.dto.query.BotQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Application use case for bot management
 * Follows Clean Architecture by defining application layer contracts
 */
public interface BotManagementUseCase {

    /**
     * Create a new bot
     */
    Bot createBot(CreateBotRequest request);

    /**
     * Update bot information
     */
    Bot updateBot(Long botId, UpdateBotRequest request);

    /**
     * Activate a bot
     */
    Bot activateBot(Long botId);

    /**
     * Deactivate a bot
     */
    Bot deactivateBot(Long botId);

    /**
     * Delete a bot
     */
    void deleteBot(Long botId);

    /**
     * Get bot by ID
     */
    Optional<Bot> getBotById(Long botId);

    /**
     * Get bot by token
     */
    Optional<Bot> getBotByToken(String botToken);

    /**
     * Get bot by username
     */
    Optional<Bot> getBotByUsername(String botUsername);

    /**
     * Get all bots with pagination
     */
    Page<Bot> getAllBots(Pageable pageable);

    /**
     * Search bots by query with pagination
     */
    Page<Bot> findAll(BotQuery query, Pageable pageable);

    /**
     * Get active bots
     */
    List<Bot> getActiveBots();

    /**
     * Get operational bots
     */
    List<Bot> getOperationalBots();

    /**
     * Get bots by status
     */
    List<Bot> getBotsByStatus(Bot.BotStatus status);

    /**
     * Get bots created by user
     */
    Page<Bot> getBotsByUser(String createdBy, Pageable pageable);

    /**
     * Process incoming message
     */
    CommandResponse processMessage(CommandRequest request);

    /**
     * Get bot statistics
     */
    BotStatistics getBotStatistics(Long botId);

    /**
     * Get all bots statistics (aggregated)
     */
    AllBotsStatistics getAllBotsStatistics();

    /**
     * Get bot history
     */
    List<BotHistory> getBotHistory(Long botId);


    Page<BotHistory> getBotHistory(Long botId, Pageable pageable);

    /**
     * Bot statistics interface
     */
    interface BotStatistics {
        Long getBotId();

        String getBotUsername();

        long getTotalCommands();

        long getActiveCommands();

        long getTotalExecutions();

        long getSuccessfulExecutions();

        long getFailedExecutions();

        double getSuccessRate();

        long getAverageExecutionTime();

        String getLastActivity();
    }

    /**
     * All bots statistics interface
     */
    interface AllBotsStatistics {
        long getTotalBots();

        long getActiveBots();

        long getTotalCommands();

        long getActiveCommands();

        long getTotalExecutions();

        long getSuccessfulExecutions();

        long getFailedExecutions();

        double getAverageSuccessRate();

        long getTotalPlugins();

        long getActivePlugins();
    }
}
