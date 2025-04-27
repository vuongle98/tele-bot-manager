package com.vuog.telebotmanager.application.usecases;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandlerUseCase {
    /**
     * Handles updates from Telegram bots
     * 
     * @param update The update object from Telegram
     * @param botId The ID of the bot that received the update
     */
    void handleUpdate(Update update, Long botId);
    
    /**
     * Handles command messages from Telegram bots
     * 
     * @param update The update object containing a command
     * @param botId The ID of the bot that received the command
     */
    void handleCommand(Update update, Long botId);
}