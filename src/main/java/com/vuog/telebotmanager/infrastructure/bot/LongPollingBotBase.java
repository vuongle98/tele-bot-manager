package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


public abstract class LongPollingBotBase extends TelegramLongPollingBot implements BotHandler {
    protected final TelegramBot bot;

    public LongPollingBotBase(TelegramBot bot) {
        super(bot.getApiToken());
        this.bot = bot;
    }

    @Override
    public String getBotUsername() {
        return bot.getName();
    }

    @Override
    public TelegramBot getBot() {
        return bot;
    }

}