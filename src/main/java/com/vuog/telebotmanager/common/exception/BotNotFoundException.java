package com.vuog.telebotmanager.common.exception;

public class BotNotFoundException extends RuntimeException {
    public BotNotFoundException(Long botId) {
        super("Bot not found with ID: " + botId);
    }

    public BotNotFoundException(String message) {
        super(message);
    }

    public BotNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}