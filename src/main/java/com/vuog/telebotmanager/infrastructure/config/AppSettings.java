package com.vuog.telebotmanager.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AppSettings {
    private AiSettings ai = new AiSettings();
    private BotDefaults botDefaults = new BotDefaults();
    private PluginDefaults pluginDefaults = new PluginDefaults();
    private SystemSettings system = new SystemSettings();

    @Getter
    @Setter
    public static class AiSettings {
        private boolean enabled;
        private String model;
        private double temperature;
        private int maxTokens;
        private int timeoutMs;
        private String apiKey;
    }

    @Getter
    @Setter
    public static class BotDefaults {
        private int timeoutSeconds;
        private int retryCount;
        private int priority;
    }

    @Getter
    @Setter
    public static class PluginDefaults {
        private int timeoutSeconds;
        private int retryCount;
        private String security;
    }

    @Getter
    @Setter
    public static class SystemSettings {
        private String logLevel;
        private int cacheTtlSeconds;
        private int maxCommandsPerBot;
        private int maxPluginsPerBot;
    }
}
