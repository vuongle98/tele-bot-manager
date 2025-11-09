package com.vuog.telebotmanager.infrastructure.telegram;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Individual Telegram bot instance that handles messages for a specific bot
 * Each bot gets its own instance that processes messages from users
 */
@RequiredArgsConstructor
@Slf4j
public class TelegramBotInstance extends TelegramLongPollingBot {

    private final Bot bot;
    private final BotInstanceHandler botHandler;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            // Delegate to the bot handler
            botHandler.handleUpdate(update);
        } catch (Exception e) {
            log.error("Error in bot handler for bot {}: {}", bot.getBotUsername(), e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return bot.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return bot.getBotToken();
    }

    /**
     * Send response to user
     */
    public void sendResponse(String chatId, CommandResponse response) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(response.getResponseText());
            message.setParseMode("HTML");

            execute(message);

            log.info("Response sent to chat {}: {}", chatId, response.getResponseText());

        } catch (TelegramApiException e) {
            log.error("Error sending response to chat {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Send error response to user
     */
    public void sendErrorResponse(String chatId, String errorMessage) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("❌ " + errorMessage);
            message.setParseMode("HTML");

            execute(message);

        } catch (TelegramApiException e) {
            log.error("Error sending error response to chat {}: {}", chatId, e.getMessage());
        }
    }
}
