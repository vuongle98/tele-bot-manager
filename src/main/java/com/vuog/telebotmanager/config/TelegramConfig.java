package com.vuog.telebotmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}