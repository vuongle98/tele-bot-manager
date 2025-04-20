package com.vuog.telebotmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotManagerApplication.class, args);
    }
}
