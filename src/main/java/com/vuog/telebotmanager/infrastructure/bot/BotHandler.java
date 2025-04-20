// BotHandler.java
package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface BotHandler {
    String getBotUsername();
    String getBotToken();
    TelegramBot getBot();

    AbsSender getSender();
    Long getBotId();
}