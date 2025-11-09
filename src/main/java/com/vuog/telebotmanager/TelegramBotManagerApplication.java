package com.vuog.telebotmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Telegram Bot Manager
 * A comprehensive bot management system with AI capabilities
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class TelegramBotManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotManagerApplication.class, args);
    }
}
