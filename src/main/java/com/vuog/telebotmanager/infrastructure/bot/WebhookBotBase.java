package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public abstract class WebhookBotBase extends TelegramWebhookBot implements BotHandler {
    protected final TelegramBot bot;

    public WebhookBotBase(TelegramBot bot) {
        super(createBotOptions(), bot.getApiToken());
        this.bot = bot;
    }

    // Implement required methods without using deprecated ones
    @Override
    public String getBotUsername() {
        return bot.getName();
    }

    @Override
    public TelegramBot getBot() {
        return bot;
    }

    @Override
    public AbsSender getSender() {
        return this;
    }

    @Override
    public Long getBotId() {
        return bot.getId();
    }

    @Override
    public abstract BotApiMethod<?> onWebhookUpdateReceived(Update update);

    private static DefaultBotOptions createBotOptions() {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setMaxThreads(4);
        options.setAllowedUpdates(List.of("message", "callback_query"));
        return options;
    }

    // Add any new API methods you need to use
    // Example of new-style method implementation
    protected void executeApiMethod(BotApiMethod<?> method) throws TelegramApiException {
        try {
            execute(method);
        } catch (TelegramApiException e) {
            log.error("Failed to execute API method", e);
            throw e;
        }
    }
}