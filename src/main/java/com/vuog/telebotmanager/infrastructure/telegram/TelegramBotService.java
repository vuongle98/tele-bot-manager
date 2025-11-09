package com.vuog.telebotmanager.infrastructure.telegram;

import com.vuog.telebotmanager.application.service.BotLifecycleService;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing Telegram bot instances
 * Handles actual Telegram bot communication and message processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService {

    private final BotRepository botRepository;
    private final BotLifecycleService botLifecycleService;
    private final BotHandlerFactory botHandlerFactory;
    private final TelegramBotsApi telegramBotsApi;

    // Cache for active bot instances
    private final Map<Long, TelegramBotInstance> activeBots = new ConcurrentHashMap<>();

    /**
     * Start a Telegram bot instance
     */
    @Transactional
    public void startTelegramBot(Long botId) {
        log.info("Starting Telegram bot instance for bot ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        // Stop existing instance if running to avoid conflicts
        if (activeBots.containsKey(botId)) {
            log.warn("Telegram bot instance already exists for bot ID: {}, stopping existing instance...", botId);
            try {
                stopTelegramBot(botId);
                // Wait longer for the bot to fully stop and Telegram to process
                Thread.sleep(3000); // Increased to 3 seconds for better reliability
                log.info("Existing bot instance stopped, proceeding with new instance...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting for bot to stop");
            } catch (Exception e) {
                log.error("Error stopping existing bot instance", e);
                // Continue anyway to attempt restart
            }
        }

        try {
            // Create bot handler using factory
            BotInstanceHandler botHandler = botHandlerFactory.createHandler(bot);

            // Create bot instance
            TelegramBotInstance botInstance = new TelegramBotInstance(bot, botHandler);

            // Set the bot instance in the handler for sending messages
            if (botHandler instanceof DefaultBotInstanceHandler) {
                ((DefaultBotInstanceHandler) botHandler).setTelegramBotInstance(botInstance);
            }

            // Register bot with Telegram API
            // This may throw TelegramApiRequestException if another instance is running
            telegramBotsApi.registerBot(botInstance);

            // Store reference
            activeBots.put(botId, botInstance);

            log.info("Telegram bot instance started successfully for bot ID: {} (username: {})",
                    botId, bot.getBotUsername());

        } catch (org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException e) {
            if (e.getErrorCode() == 409) {
                log.error("Conflict: Another bot instance is already running for bot ID: {}. " +
                        "This usually happens when:\n" +
                        "1. Another instance of the application is running\n" +
                        "2. The bot is still registered from a previous session\n" +
                        "3. Webhook mode is enabled (conflicts with long polling)\n" +
                        "Please ensure only one instance is running and no webhook is set.", botId);
                activeBots.remove(botId);
                throw new RuntimeException("Telegram bot conflict: Another instance is running. " +
                        "Make sure only one bot instance is running and webhook is not enabled.", e);
            } else {
                log.error("Error starting Telegram bot instance for bot ID: {}", botId, e);
                activeBots.remove(botId);
                throw new RuntimeException("Failed to start Telegram bot instance", e);
            }
        } catch (Exception e) {
            log.error("Error starting Telegram bot instance for bot ID: {}", botId, e);
            activeBots.remove(botId);
            throw new RuntimeException("Failed to start Telegram bot instance", e);
        }
    }

    /**
     * Stop a Telegram bot instance
     */
    @Transactional
    public void stopTelegramBot(Long botId) {
        log.info("Stopping Telegram bot instance for bot ID: {}", botId);

        try {
            // Get bot instance
            TelegramBotInstance botInstance = activeBots.get(botId);

            if (botInstance != null) {
                try {
                    // Clear webhook and close bot session to properly deregister
                    log.info("Clearing bot session and closing connection for bot ID: {}", botId);
                    
                    // Delete webhook to ensure clean state
                    try {
                        org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook deleteWebhook = 
                            new org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook();
                        deleteWebhook.setDropPendingUpdates(true);
                        botInstance.execute(deleteWebhook);
                        log.info("Webhook deleted for bot ID: {}", botId);
                    } catch (Exception e) {
                        log.warn("Could not delete webhook for bot {}: {}", botId, e.getMessage());
                    }
                    
                } catch (Exception e) {
                    log.warn("Error clearing bot session: {}", e.getMessage());
                }
            }

            // Remove from active bots - this must happen after clearing
            activeBots.remove(botId);

            log.info("Telegram bot instance stopped successfully for bot ID: {}", botId);

        } catch (Exception e) {
            log.error("Error stopping Telegram bot instance for bot ID: {}", botId, e);
            // Still remove from active bots even if there's an error
            activeBots.remove(botId);
            throw new RuntimeException("Failed to stop Telegram bot instance", e);
        }
    }

    /**
     * Restart a Telegram bot instance
     */
    @Transactional
    public void restartTelegramBot(Long botId) {
        log.info("Restarting Telegram bot instance for bot ID: {}", botId);

        stopTelegramBot(botId);
        
        // Wait for Telegram to fully process the bot stop before restarting
        // This prevents 409 conflicts
        try {
            log.info("Waiting for bot to fully stop before restart...");
            Thread.sleep(2000); // Wait 2 seconds for Telegram to process
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting for bot to stop");
        }
        
        startTelegramBot(botId);
    }

    /**
     * Check if Telegram bot is running
     */
    public boolean isTelegramBotRunning(Long botId) {
        return activeBots.containsKey(botId);
    }

    /**
     * Get active bot instances
     */
    public Map<Long, TelegramBotInstance> getActiveBots() {
        return Map.copyOf(activeBots);
    }
}
