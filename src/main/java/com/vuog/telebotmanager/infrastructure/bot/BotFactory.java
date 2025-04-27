package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.infrastructure.bot.handler.LongPollingBotHandler;
import com.vuog.telebotmanager.infrastructure.bot.handler.WebhookBotHandler;
import org.springframework.stereotype.Component;

/**
 * Factory for creating different types of bot handlers
 */
@Component
public class BotFactory {
    
    /**
     * Creates the appropriate bot handler for the given bot
     * @param bot The bot to create a handler for
     * @return A bot handler
     */
    public BotHandler createHandler(TelegramBot bot) {
        // You can implement logic to determine which type of bot to create
        // For now, we'll create a LongPollingBotHandler
        return new LongPollingBotHandler(bot);
    }
    
    /**
     * Creates a webhook bot handler
     * @param bot The bot to create a handler for
     * @return A webhook bot handler
     */
    public WebhookBotHandler createWebhookHandler(TelegramBot bot) {
        return new WebhookBotHandler(bot);
    }
    
    /**
     * Creates a long polling bot handler
     * @param bot The bot to create a handler for
     * @return A long polling bot handler
     */
    public LongPollingBotHandler createLongPollingHandler(TelegramBot bot) {
        return new LongPollingBotHandler(bot);
    }
}
