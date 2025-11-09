package com.vuog.telebotmanager.infrastructure.service;

import com.vuog.telebotmanager.application.usecase.ConfigurationUseCase;
import com.vuog.telebotmanager.infrastructure.config.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationLoaderService {

    private final ConfigurationUseCase configurationUseCase;
    private final AppSettings appSettings;

    public void load() {
        log.info("Loading application settings from database configurations...");
        // AI
        appSettings.getAi().setEnabled(getBoolean("ai.google.enabled", true));
        appSettings.getAi().setModel(getString("ai.google.model", "gemini-pro"));
        appSettings.getAi().setTemperature(getDouble("ai.google.temperature", 0.7));
        appSettings.getAi().setMaxTokens(getInt("ai.google.max_tokens", 1000));
        appSettings.getAi().setTimeoutMs(getInt("ai.google.timeout", 30000));
        appSettings.getAi().setApiKey(getString("ai.google.api_key", ""));

        // Bot defaults
        appSettings.getBotDefaults().setTimeoutSeconds(getInt("bot.default.timeout", 30));
        appSettings.getBotDefaults().setRetryCount(getInt("bot.default.retry_count", 3));
        appSettings.getBotDefaults().setPriority(getInt("bot.default.priority", 100));

        // Plugin defaults
        appSettings.getPluginDefaults().setTimeoutSeconds(getInt("plugin.default.timeout", 60));
        appSettings.getPluginDefaults().setRetryCount(getInt("plugin.default.retry_count", 2));
        appSettings.getPluginDefaults().setSecurity(getString("plugin.default.security", "strict"));

        // System
        appSettings.getSystem().setLogLevel(getString("system.log_level", "INFO"));
        appSettings.getSystem().setCacheTtlSeconds(getInt("system.cache_ttl", 600));
        appSettings.getSystem().setMaxCommandsPerBot(getInt("system.max_commands_per_bot", 100));
        appSettings.getSystem().setMaxPluginsPerBot(getInt("system.max_plugins_per_bot", 50));

        log.info("Application settings loaded");
    }

    private String getString(String key, String def) {
        try {
            return configurationUseCase.getConfigurationValue(key).orElse(def);
        } catch (Exception e) {
            log.warn("Failed to read config key={}, using default: {}. Error: {}", key, def, e.getMessage());
            return def;
        }
    }

    private int getInt(String key, int def) {
        try {
            return Integer.parseInt(configurationUseCase.getConfigurationValue(key).orElse(String.valueOf(def)));
        } catch (Exception e) {
            log.warn("Failed to parse int for key={}, using default: {}. Error: {}", key, def, e.getMessage());
            return def;
        }
    }

    private double getDouble(String key, double def) {
        try {
            return Double.parseDouble(configurationUseCase.getConfigurationValue(key).orElse(String.valueOf(def)));
        } catch (Exception e) {
            log.warn("Failed to parse double for key={}, using default: {}. Error: {}", key, def, e.getMessage());
            return def;
        }
    }

    private boolean getBoolean(String key, boolean def) {
        try {
            return Boolean.parseBoolean(configurationUseCase.getConfigurationValue(key).orElse(String.valueOf(def)));
        } catch (Exception e) {
            log.warn("Failed to parse boolean for key={}, using default: {}. Error: {}", key, def, e.getMessage());
            return def;
        }
    }
}
