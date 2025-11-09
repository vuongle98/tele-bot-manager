package com.vuog.telebotmanager.infrastructure.service;

import com.vuog.telebotmanager.application.service.BotOrchestrationService;
import com.vuog.telebotmanager.application.service.ConfigurationService;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotHistory;
import com.vuog.telebotmanager.domain.entity.BotRuntimeState;
import com.vuog.telebotmanager.domain.repository.BotHistoryRepository;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.BotRuntimeStateRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Service for initializing the system on startup
 * Sets up default configurations and prepares the system
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SystemInitializationService implements ApplicationRunner {

    private final ConfigurationService configurationService;
    private final BotRuntimeStateRepository botRuntimeStateRepository;
    private final BotOrchestrationService botOrchestrationService;
    private final BotRepository botRepository;
    private final BotHistoryRepository botHistoryRepository;
    private final ConfigurationLoaderService configurationLoaderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing Telegram Bot Manager system...");

        try {
            // Initialize default configurations
            initializeSystem();

            log.info("Telegram Bot Manager system initialized successfully");

        } catch (Exception e) {
            log.error("Error initializing system", e);
            throw e;
        }
    }

    private void initializeSystem() {
        log.info("Initializing system configurations...");

        // Initialize default configurations
        configurationService.initializeDefaultConfigurations();

        // Load runtime settings from database into AppSettings bean
        try {
            configurationLoaderService.load();
        } catch (Exception e) {
            log.warn("Failed to load application settings from DB: {}", e.getMessage());
        }

        try {
            // Restore bots with suspended status
            var suspendedBots = botRepository.findByStatus(Bot.BotStatus.SUSPENDED);
            if (!suspendedBots.isEmpty()) {
                log.info("Restoring {} bot(s) with SUSPENDED status...", suspendedBots.size());
                for (Bot bot : suspendedBots) {
                    try {
                        botOrchestrationService.startBot(bot.getId());
                        
                        // Create history record for bot restore
                        BotHistory history = BotHistory.createStatusChange(
                                bot, Bot.BotStatus.SUSPENDED, Bot.BotStatus.ACTIVE, "System", "Bot restored on application startup");
                        botHistoryRepository.save(history);
                    } catch (Exception e) {
                        log.error("Failed to restore suspended bot {}: {}", bot.getId(), e.getMessage());
                        
                        // Create history record for restoration failure
                        try {
                            BotHistory errorHistory = BotHistory.createErrorRecord(
                                    bot, bot.getStatus(), "Failed to restore bot on startup: " + e.getMessage(), "System");
                            botHistoryRepository.save(errorHistory);
                        } catch (Exception historyError) {
                            log.warn("Failed to save error history for bot {}: {}", bot.getId(), historyError.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Bot restore step skipped due to error: {}", e.getMessage());
        }

        log.info("System configurations initialized");
    }

    @PreDestroy
    public void onShutdown() {
        log.info("Application is shutting down...");
        try {
            var activeBots = botRepository.findByStatus(Bot.BotStatus.ACTIVE);
            for (Bot bot : activeBots) {
                try {
                    Bot.BotStatus previousStatus = bot.getStatus();
                    bot.suspended();
                    botRepository.save(bot);
                    
                    // Create history record for shutdown
                    BotHistory history = BotHistory.createStatusChange(
                            bot, previousStatus, Bot.BotStatus.SUSPENDED, "System", "Bot suspended during application shutdown");
                    botHistoryRepository.save(history);
                    
                    var state = botRuntimeStateRepository.findById(bot.getId()).orElse(null);
                    if (state != null) {
                        state.setIsRunning(false);
                        state.setLastStoppedAt(java.time.LocalDateTime.now());
                        botRuntimeStateRepository.save(state);
                    }
                } catch (Exception e) {
                    log.error("Error shutting down bot {}: {}", bot.getId(), e.getMessage());
                }
            }
            log.info("Application shutdown completed");
        } catch (Exception e) {
            log.warn("Error during shutdown hook: {}", e.getMessage());
        }
    }
}
