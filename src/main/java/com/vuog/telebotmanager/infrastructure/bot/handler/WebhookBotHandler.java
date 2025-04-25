// WebhookBotHandler.java
package com.vuog.telebotmanager.infrastructure.bot.handler;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

@Log4j2
public class WebhookBotHandler extends WebhookBotBase {

    public WebhookBotHandler(TelegramBot bot) {
        super(bot);
    }

    @Override
    public String getBotPath() {
        return "/webhook/" + bot.getId();
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
                log.error("Error processing webhook update: {}", e.getMessage());
                return null;
            }
        }
        return null;
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

    // WebhookBotHandler.java
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        log.info("Setting webhook for bot {}: {}", bot.getId(), setWebhook.getUrl());
        try {
            super.setWebhook(setWebhook);
            log.info("Successfully set webhook for bot {}", bot.getId());
        } catch (TelegramApiException e) {
            log.error("Failed to set webhook for bot {}: {}", bot.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void onRegister() {
        log.info("Bot {} registered successfully", bot.getId());
        try {
            if (bot.getConfiguration().getUpdateMethod() == TelegramBot.UpdateMethod.WEBHOOK) {
                SetWebhook setWebhook = SetWebhook.builder()
                    .url(bot.getConfiguration().getWebhookUrl())
                    .maxConnections(bot.getConfiguration().getMaxConnections())
                    .allowedUpdates(Arrays.asList(bot.getConfiguration().getAllowedUpdates().split(",")))
                    .build();
                setWebhook(setWebhook);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to configure webhook during registration for bot {}", bot.getId(), e);
        }
    }
}