package com.vuog.telebotmanager.domain.bot.ports;

public interface TelegramMessageSender {
    /**
     * Sends a message via the appropriate Telegram bot
     * @param botId The ID of the bot to send the message with
     * @param chatId The chat ID to send the message to
     * @param message The text message to send
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendMessage(Long botId, String chatId, String message);
}
