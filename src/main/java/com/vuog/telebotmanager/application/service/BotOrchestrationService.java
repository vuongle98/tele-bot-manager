package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotRuntimeState;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.BotRuntimeStateRepository;
import com.vuog.telebotmanager.infrastructure.telegram.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for orchestrating bot lifecycle operations
 * Coordinates between BotLifecycleService and TelegramBotService to avoid circular dependencies
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BotOrchestrationService {

    private final BotRepository botRepository;
    private final BotRuntimeStateRepository botRuntimeStateRepository;
    private final BotLifecycleService botLifecycleService;
    private final TelegramBotService telegramBotService;

    /**
     * Start a bot with full lifecycle management
     */
    public Bot startBot(Long botId) {
        log.info("Starting bot with full lifecycle for bot ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        if (bot.isOperational()) {
            log.warn("Bot {} is already operational", botId);
            return bot;
        }

        try {
            // 1. Start bot lifecycle (load commands and plugins)
            Bot lifecycleBot = botLifecycleService.startBot(botId);

            // 2. Start Telegram bot instance
            telegramBotService.startTelegramBot(botId);

            // 3. Persist runtime state
            BotRuntimeState state = botRuntimeStateRepository.findById(botId).orElseGet(() -> BotRuntimeState.builder()
                    .botId(botId)
                    .build());
            state.setIsRunning(true);
            state.setLastStartedAt(java.time.LocalDateTime.now());
            state.setLastError(null);
            botRuntimeStateRepository.save(state);

            log.info("Bot {} started successfully with full lifecycle", botId);
            return lifecycleBot;

        } catch (Exception e) {
            log.error("Error starting bot: {}", botId, e);
            bot.markAsError();
            botRepository.save(bot);
            // Persist error state
            BotRuntimeState state = botRuntimeStateRepository.findById(botId).orElseGet(() -> BotRuntimeState.builder()
                    .botId(botId)
                    .build());
            state.setIsRunning(false);
            state.setLastError(e.getMessage());
            botRuntimeStateRepository.save(state);
            throw new RuntimeException("Failed to start bot", e);
        }
    }

    /**
     * Stop a bot with full lifecycle management
     */
    public Bot stopBot(Long botId) {
        log.info("Stopping bot with full lifecycle for bot ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        if (!bot.isOperational()) {
            log.warn("Bot {} is not operational", botId);
            return bot;
        }

        try {
            // 1. Stop Telegram bot instance
            telegramBotService.stopTelegramBot(botId);

            // 2. Stop bot lifecycle (unload commands and plugins)
            Bot lifecycleBot = botLifecycleService.stopBot(botId);

            // 3. Persist runtime state
            BotRuntimeState state = botRuntimeStateRepository.findById(botId).orElseGet(() -> BotRuntimeState.builder()
                    .botId(botId)
                    .build());
            state.setIsRunning(false);
            state.setLastStoppedAt(java.time.LocalDateTime.now());
            botRuntimeStateRepository.save(state);

            log.info("Bot {} stopped successfully with full lifecycle", botId);
            return lifecycleBot;

        } catch (Exception e) {
            log.error("Error stopping bot: {}", botId, e);
            bot.markAsError();
            botRepository.save(bot);
            // Persist error state
            BotRuntimeState state = botRuntimeStateRepository.findById(botId).orElseGet(() -> BotRuntimeState.builder()
                    .botId(botId)
                    .build());
            state.setIsRunning(false);
            state.setLastError(e.getMessage());
            botRuntimeStateRepository.save(state);
            throw new RuntimeException("Failed to stop bot", e);
        }
    }

    /**
     * Restart a bot with full lifecycle management
     */
    public Bot restartBot(Long botId) {
        log.info("Restarting bot with full lifecycle for bot ID: {}", botId);

        stopBot(botId);
        
        // Wait for Telegram API to fully process the bot stop before restarting
        // This prevents 409 conflicts when restarting bots
        try {
            log.info("Waiting for bot stop to fully propagate to Telegram API...");
            Thread.sleep(2000); // Wait 2 seconds for Telegram to process
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting for bot restart");
        }
        
        return startBot(botId);
    }

    /**
     * Get bot status with full lifecycle information
     */
    @Transactional(readOnly = true)
    public BotLifecycleService.BotStatusInfo getBotStatus(Long botId) {
        return botLifecycleService.getBotStatus(botId);
    }

    /**
     * Check if bot is running with full lifecycle
     */
    @Transactional(readOnly = true)
    public boolean isBotRunning(Long botId) {
        return botLifecycleService.isBotRunning(botId) &&
                telegramBotService.isTelegramBotRunning(botId);
    }
}
