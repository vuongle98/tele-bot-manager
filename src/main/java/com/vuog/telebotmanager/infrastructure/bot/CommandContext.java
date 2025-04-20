package com.vuog.telebotmanager.infrastructure.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
public record CommandContext(String chatId, Long userId, String args, Message message, BotHandler botHandler) {
    public void reply(String text) {
        try {
            botHandler.getSender().execute(new SendMessage(chatId, text));
        } catch (Exception e) {
            log.error("Failed to send reply to chat {} using bot {}: {}", chatId, botHandler.getBotId(), e.getMessage());
        }
    }
}
