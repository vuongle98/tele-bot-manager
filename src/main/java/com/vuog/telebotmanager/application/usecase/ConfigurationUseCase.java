package com.vuog.telebotmanager.application.usecase;

import com.vuog.telebotmanager.domain.entity.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfigurationUseCase {

    Optional<String> getConfigurationValue(String key);

    String getConfigurationValue(String key, String defaultValue);

    void setConfigurationValue(String key, String value);

    Map<String, String> getBotConfigurations(Long botId);

    void setBotConfiguration(Long botId, String key, String value);

    Map<String, String> getAiConfigurations();

    void setAiConfiguration(String key, String value);

    Map<String, String> getPluginConfigurations(String pluginName);

    void setPluginConfiguration(String pluginName, String key, String value);

    Map<String, String> getGlobalConfigurations();

    List<Configuration> getConfigurationsByScope(Configuration.ConfigurationScope scope);

    List<Configuration> getConfigurationsByType(Configuration.ConfigurationType type);

    void initializeDefaultConfigurations();

    void deactivateConfiguration(String key);

    void activateConfiguration(String key);

    void deleteConfiguration(String key);

    ConfigurationServiceStats getConfigurationStats();

    interface ConfigurationServiceStats {
        long getTotalConfigurations();
        long getActiveConfigurations();
        long getGlobalConfigurations();
        long getBotConfigurations();
        long getAiConfigurations();
        long getPluginConfigurations();
    }
}
