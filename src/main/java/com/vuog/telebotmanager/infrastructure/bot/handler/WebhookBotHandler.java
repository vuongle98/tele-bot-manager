package com.vuog.telebotmanager.infrastructure.bot.handler;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;


@Slf4j
public class WebhookBotHandler extends WebhookBotBase {

    public WebhookBotHandler(TelegramBot bot) {
        super(bot);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            try {
                if (text.startsWith("/")) {
                    return handleCommand(message);
                } else {
                    return handleMessage(message);
                }
            } catch (Exception e) {
                log.error("Error processing update: {}", e.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return bot.getName(); // Use bot name as path
    }

    @Override
    public AbsSender getSender() {
        return this;
    }

    @Override
    public Long getBotId() {
        return bot.getId();
    }

    private BotApiMethod<?> handleCommand(Message message) {
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId());
        response.setText("Command received: " + message.getText());
        return response;
    }

    private BotApiMethod<?> handleMessage(Message message) {
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId());
        response.setText("Message received: " + message.getText());
        return response;
    }
}