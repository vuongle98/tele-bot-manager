package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.repository.PluginRepository;
import com.vuog.telebotmanager.domain.service.PluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing bot lifecycle and command loading
 * Handles bot startup, shutdown, and command registration
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BotLifecycleService {

    private final BotRepository botRepository;
    private final CommandRepository commandRepository;
    private final PluginRepository pluginRepository;
    private final PluginManager pluginManager;

    // In-memory cache for loaded bot commands
    private final Map<Long, List<Command>> botCommandsCache = new ConcurrentHashMap<>();
    private final Map<Long, List<BotPlugin>> botPluginsCache = new ConcurrentHashMap<>();

    /**
     * Start a bot and load all its commands and plugins
     */
    public Bot startBot(Long botId) {
        log.info("Starting bot with ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        if (bot.isOperational()) {
            log.warn("Bot {} is already operational", botId);
            return bot;
        }

        try {
            // Load bot commands
            loadBotCommands(botId);

            // Load bot plugins
            loadBotPlugins(botId);

            // Activate the bot
            bot.activate();
            botRepository.save(bot);

            log.info("Bot {} started successfully with {} commands and {} plugins",
                    botId,
                    botCommandsCache.getOrDefault(botId, List.of()).size(),
                    botPluginsCache.getOrDefault(botId, List.of()).size());

            return bot;

        } catch (Exception e) {
            log.error("Error starting bot: {}", botId, e);
            bot.markAsError();
            botRepository.save(bot);
            throw new RuntimeException("Failed to start bot", e);
        }
    }

    /**
     * Stop a bot and unload all its commands and plugins
     */
    public Bot stopBot(Long botId) {
        log.info("Stopping bot with ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        if (!bot.isOperational()) {
            log.warn("Bot {} is not operational", botId);
            return bot;
        }

        try {
            // Unload bot plugins
            unloadBotPlugins(botId);

            // Clear bot commands cache
            botCommandsCache.remove(botId);
            botPluginsCache.remove(botId);

            // Deactivate the bot
            bot.deactivate();
            botRepository.save(bot);

            log.info("Bot {} stopped successfully", botId);
            return bot;

        } catch (Exception e) {
            log.error("Error stopping bot: {}", botId, e);
            bot.markAsError();
            botRepository.save(bot);
            throw new RuntimeException("Failed to stop bot", e);
        }
    }

    /**
     * Restart a bot (stop and start)
     */
    public Bot restartBot(Long botId) {
        log.info("Restarting bot with ID: {}", botId);

        stopBot(botId);
        return startBot(botId);
    }

    /**
     * Load all commands for a bot
     */
    private void loadBotCommands(Long botId) {
        log.info("Loading commands for bot: {}", botId);

        List<Command> combined = commandRepository.findEnabledCommandsByBotIdOrGlobal(botId);
        // Deduplicate by command string, preferring bot-specific over global
        java.util.Map<String, Command> byName = new java.util.LinkedHashMap<>();
        for (Command c : combined) {
            String key = c.getCommand();
            if (!byName.containsKey(key)) {
                byName.put(key, c);
            } else {
                Command existing = byName.get(key);
                boolean existingIsGlobal = existing.getBot() == null;
                boolean currentIsBotSpecific = c.getBot() != null;
                if (existingIsGlobal && currentIsBotSpecific) {
                    byName.put(key, c);
                }
            }
        }
        List<Command> deduped = new java.util.ArrayList<>(byName.values());
        botCommandsCache.put(botId, deduped);

        log.info("Loaded {} commands for bot: {} (including global)", deduped.size(), botId);
    }

    /**
     * Load all plugins for a bot
     */
    private void loadBotPlugins(Long botId) {
        log.info("Loading plugins for bot: {}", botId);

        // Get all active plugins (for now, we'll load all active plugins)
        // In a more sophisticated system, you might have bot-specific plugins
        List<BotPlugin> plugins = pluginRepository.findExecutablePlugins();

        for (BotPlugin plugin : plugins) {
            try {
                if (!pluginManager.isPluginLoaded(plugin.getName())) {
                    pluginManager.loadPlugin(plugin.getId());
                }
            } catch (Exception e) {
                log.error("Error loading plugin {} for bot {}", plugin.getName(), botId, e);
            }
        }

        botPluginsCache.put(botId, plugins);

        log.info("Loaded {} plugins for bot: {}", plugins.size(), botId);
    }

    /**
     * Unload all plugins for a bot
     */
    private void unloadBotPlugins(Long botId) {
        log.info("Unloading plugins for bot: {}", botId);

        List<BotPlugin> plugins = botPluginsCache.get(botId);
        if (plugins != null) {
            for (BotPlugin plugin : plugins) {
                try {
                    if (pluginManager.isPluginLoaded(plugin.getName())) {
                        pluginManager.unloadPlugin(plugin.getId());
                    }
                } catch (Exception e) {
                    log.error("Error unloading plugin {} for bot {}", plugin.getName(), botId, e);
                }
            }
        }

        log.info("Unloaded plugins for bot: {}", botId);
    }

    /**
     * Get loaded commands for a bot
     */
    @Transactional(readOnly = true)
    public List<Command> getBotCommands(Long botId) {
        return botCommandsCache.getOrDefault(botId, List.of());
    }

    /**
     * Get loaded plugins for a bot
     */
    @Transactional(readOnly = true)
    public List<BotPlugin> getBotPlugins(Long botId) {
        return botPluginsCache.getOrDefault(botId, List.of());
    }

    /**
     * Reload commands for a bot
     */
    public void reloadBotCommands(Long botId) {
        log.info("Reloading commands for bot: {}", botId);
        loadBotCommands(botId);
    }

    /**
     * Reload plugins for a bot
     */
    public void reloadBotPlugins(Long botId) {
        log.info("Reloading plugins for bot: {}", botId);
        unloadBotPlugins(botId);
        loadBotPlugins(botId);
    }

    /**
     * Check if bot is running
     */
    @Transactional(readOnly = true)
    public boolean isBotRunning(Long botId) {
        Bot bot = botRepository.findById(botId).orElse(null);
        return bot != null && bot.isOperational();
    }

    /**
     * Get bot status information
     */
    @Transactional(readOnly = true)
    public BotStatusInfo getBotStatus(Long botId) {
        Bot bot = botRepository.findById(botId).orElse(null);
        if (bot == null) {
            return null;
        }

        List<Command> commands = getBotCommands(botId);
        List<BotPlugin> plugins = getBotPlugins(botId);

        return BotStatusInfo.builder()
                .botId(botId)
                .botUsername(bot.getBotUsername())
                .status(bot.getStatus())
                .isActive(bot.getIsActive())
                .isOperational(bot.isOperational())
                .commandCount(commands.size())
                .pluginCount(plugins.size())
                .lastStarted(bot.getUpdatedAt())
                .build();
    }

    /**
     * Bot status information
     */
    @lombok.Data
    @lombok.Builder
    public static class BotStatusInfo {
        private Long botId;
        private String botUsername;
        private Bot.BotStatus status;
        private Boolean isActive;
        private Boolean isOperational;
        private Integer commandCount;
        private Integer pluginCount;
        private java.time.LocalDateTime lastStarted;
    }
}
