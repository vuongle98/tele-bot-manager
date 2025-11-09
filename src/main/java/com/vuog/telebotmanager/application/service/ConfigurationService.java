package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.usecase.ConfigurationUseCase;
import com.vuog.telebotmanager.domain.entity.Configuration;
import com.vuog.telebotmanager.domain.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing database-driven configuration
 * Handles all configuration storage and retrieval
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfigurationService implements ConfigurationUseCase {

    private final ConfigurationRepository configurationRepository;

    /**
     * Get configuration value by key
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "configurations", key = "#key")
    public Optional<String> getConfigurationValue(String key) {
        return configurationRepository.findByKeyName(key)
                .filter(Configuration::getIsActive)
                .map(Configuration::getValue);
    }

    /**
     * Get configuration value with default
     */
    @Transactional(readOnly = true)
    public String getConfigurationValue(String key, String defaultValue) {
        return getConfigurationValue(key).orElse(defaultValue);
    }

    /**
     * Set configuration value
     */
    public void setConfigurationValue(String key, String value, String updatedBy) {
        String actor = updatedBy != null ? updatedBy : "system";
        Optional<Configuration> existingConfig = configurationRepository.findByKeyName(key);

        if (existingConfig.isPresent()) {
            Configuration config = existingConfig.get();
            config.updateValue(value, actor);
            configurationRepository.save(config);
        } else {
            // Create new configuration
            Configuration newConfig = Configuration.builder()
                    .id(key)
                    .keyName(key)
                    .value(value)
                    .type(Configuration.ConfigurationType.STRING)
                    .scope(Configuration.ConfigurationScope.GLOBAL)
                    .isActive(true)
                    .version("1.0.0")
                    .createdBy(actor)
                    .updatedBy(actor)
                    .build();

            configurationRepository.save(newConfig);
        }

        log.info("Configuration updated: {} = {}", key, value);
    }

    @Override
    public void setConfigurationValue(String key, String value) {
        setConfigurationValue(key, value, null);
    }

    /**
     * Get bot-specific configuration
     */
    @Transactional(readOnly = true)
    public Map<String, String> getBotConfigurations(Long botId) {
        return configurationRepository.findByScopeAndIsActive(
                        Configuration.ConfigurationScope.BOT_SPECIFIC, true)
                .stream()
                .filter(config -> config.getKeyName().startsWith("bot." + botId + "."))
                .collect(Collectors.toMap(
                        config -> config.getKeyName().substring(("bot." + botId + ".").length()),
                        Configuration::getValue
                ));
    }

    /**
     * Set bot-specific configuration
     */
    public void setBotConfiguration(Long botId, String key, String value, String updatedBy) {
        String fullKey = "bot." + botId + "." + key;
        setConfigurationValue(fullKey, value, updatedBy);
    }

    @Override
    public void setBotConfiguration(Long botId, String key, String value) {
        String fullKey = "bot." + botId + "." + key;
        setConfigurationValue(fullKey, value, null);
    }

    /**
     * Get AI configuration
     */
    @Transactional(readOnly = true)
    public Map<String, String> getAiConfigurations() {
        return configurationRepository.findAiConfigurations()
                .stream()
                .collect(Collectors.toMap(
                        config -> config.getKeyName().substring("ai.".length()),
                        Configuration::getValue
                ));
    }

    /**
     * Set AI configuration
     */
    public void setAiConfiguration(String key, String value, String updatedBy) {
        String fullKey = "ai." + key;
        setConfigurationValue(fullKey, value, updatedBy);
    }

    @Override
    public void setAiConfiguration(String key, String value) {
        String fullKey = "ai." + key;
        setConfigurationValue(fullKey, value, null);
    }

    /**
     * Get plugin configuration
     */
    @Transactional(readOnly = true)
    public Map<String, String> getPluginConfigurations(String pluginName) {
        return configurationRepository.findPluginConfigurations()
                .stream()
                .filter(config -> config.getKeyName().startsWith("plugin." + pluginName + "."))
                .collect(Collectors.toMap(
                        config -> config.getKeyName().substring(("plugin." + pluginName + ".").length()),
                        Configuration::getValue
                ));
    }

    /**
     * Set plugin configuration
     */
    public void setPluginConfiguration(String pluginName, String key, String value, String updatedBy) {
        String fullKey = "plugin." + pluginName + "." + key;
        setConfigurationValue(fullKey, value, updatedBy);
    }

    @Override
    public void setPluginConfiguration(String pluginName, String key, String value) {
        String fullKey = "plugin." + pluginName + "." + key;
        setConfigurationValue(fullKey, value, null);
    }

    /**
     * Get all global configurations
     */
    @Transactional(readOnly = true)
    public Map<String, String> getGlobalConfigurations() {
        return configurationRepository.findGlobalConfigurations()
                .stream()
                .collect(Collectors.toMap(
                        Configuration::getKeyName,
                        Configuration::getValue
                ));
    }

    /**
     * Initialize default configurations
     */
    public void initializeDefaultConfigurations() {
        log.info("Initializing default configurations");

        // AI Configuration
        setAiConfiguration("enabled", "true", "system");
        setAiConfiguration("model", "gemini-pro", "system");
        setAiConfiguration("temperature", "0.7", "system");
        setAiConfiguration("max_tokens", "1000", "system");
        setAiConfiguration("timeout", "30000", "system");

        // Bot Configuration
        setConfigurationValue("bot.default.timeout", "30", "system");
        setConfigurationValue("bot.default.retry_count", "3", "system");
        setConfigurationValue("bot.default.priority", "100", "system");

        // Plugin Configuration
        setConfigurationValue("plugin.default.timeout", "60", "system");
        setConfigurationValue("plugin.default.retry_count", "2", "system");
        setConfigurationValue("plugin.default.security", "strict", "system");

        // System Configuration
        setConfigurationValue("system.log_level", "INFO", "system");
        setConfigurationValue("system.cache_ttl", "600", "system");
        setConfigurationValue("system.max_commands_per_bot", "100", "system");
        setConfigurationValue("system.max_plugins_per_bot", "50", "system");

        log.info("Default configurations initialized");
    }

    /**
     * Get configuration by scope
     */
    @Transactional(readOnly = true)
    public List<Configuration> getConfigurationsByScope(Configuration.ConfigurationScope scope) {
        return configurationRepository.findByScopeAndIsActive(scope, true);
    }

    /**
     * Get configuration by type
     */
    @Transactional(readOnly = true)
    public List<Configuration> getConfigurationsByType(Configuration.ConfigurationType type) {
        return configurationRepository.findByTypeAndIsActive(type, true);
    }

    /**
     * Deactivate configuration
     */
    public void deactivateConfiguration(String key) {
        configurationRepository.findByKeyName(key)
                .ifPresent(config -> {
                    config.deactivate();
                    configurationRepository.save(config);
                });
    }

    /**
     * Activate configuration
     */
    public void activateConfiguration(String key) {
        configurationRepository.findByKeyName(key)
                .ifPresent(config -> {
                    config.activate();
                    configurationRepository.save(config);
                });
    }

    /**
     * Delete configuration
     */
    public void deleteConfiguration(String key) {
        configurationRepository.findByKeyName(key)
                .ifPresent(configurationRepository::delete);
    }

    /**
     * Get configuration statistics
     */
    @Transactional(readOnly = true)
    public ConfigurationUseCase.ConfigurationServiceStats getConfigurationStats() {
        long totalConfigs = configurationRepository.count();
        long activeConfigs = configurationRepository.findByIsActiveTrue().size();
        long globalConfigs = configurationRepository.countByScope(Configuration.ConfigurationScope.GLOBAL);
        long botConfigs = configurationRepository.countByScope(Configuration.ConfigurationScope.BOT_SPECIFIC);
        long aiConfigs = configurationRepository.countByScope(Configuration.ConfigurationScope.AI_SPECIFIC);
        long pluginConfigs = configurationRepository.countByScope(Configuration.ConfigurationScope.PLUGIN_SPECIFIC);

        return ConfigurationStats.builder()
                .totalConfigurations(totalConfigs)
                .activeConfigurations(activeConfigs)
                .globalConfigurations(globalConfigs)
                .botConfigurations(botConfigs)
                .aiConfigurations(aiConfigs)
                .pluginConfigurations(pluginConfigs)
                .build();
    }

    /**
     * Configuration statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class ConfigurationStats implements ConfigurationUseCase.ConfigurationServiceStats {
        private long totalConfigurations;
        private long activeConfigurations;
        private long globalConfigurations;
        private long botConfigurations;
        private long aiConfigurations;
        private long pluginConfigurations;
    }
}
