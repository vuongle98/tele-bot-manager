package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotHandlerFactory {

    public BotHandler createHandler(TelegramBot bot) {

        return switch (bot.getConfiguration().getUpdateMethod()) {
            case WEBHOOK -> new WebhookBotHandler(bot);
            case LONG_POLLING -> new LongPollingBotHandler(bot);
        };
    }
}
