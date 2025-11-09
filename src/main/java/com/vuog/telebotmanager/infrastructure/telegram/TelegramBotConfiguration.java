package com.vuog.telebotmanager.infrastructure.telegram;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Configuration for Telegram bot integration
 * Provides necessary beans for Telegram bot functionality
 */
@Configuration
public class TelegramBotConfiguration {

    /**
     * Creates a TelegramBotsApi bean for managing bot instances
     */
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            return new TelegramBotsApi(DefaultBotSession.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create TelegramBotsApi", e);
        }
    }
}
