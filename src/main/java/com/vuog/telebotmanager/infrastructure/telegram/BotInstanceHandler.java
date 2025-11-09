package com.vuog.telebotmanager.infrastructure.telegram;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Abstract handler for bot instances
 * Allows different implementations for different bot types
 */
@Getter
public abstract class BotInstanceHandler {

    /**
     * Get bot information
     */
    protected final Bot bot;

    public BotInstanceHandler(Bot bot) {
        this.bot = bot;
    }

    /**
     * Handle incoming update from Telegram
     */
    public abstract void handleUpdate(Update update);

    /**
     * Process command request
     */
    public abstract CommandResponse processCommand(CommandRequest request);

    /**
     * Get bot username
     */
    public String getBotUsername() {
        return bot.getBotUsername();
    }

    /**
     * Get bot token
     */
    public String getBotToken() {
        return bot.getBotToken();
    }

    /**
     * Check if bot is active
     */
    public boolean isActive() {
        return bot.getIsActive() && bot.getStatus() == Bot.BotStatus.ACTIVE;
    }
}
