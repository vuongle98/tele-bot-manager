package com.vuog.telebotmanager.infrastructure.telegram;

import com.vuog.telebotmanager.application.service.BotLifecycleService;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.infrastructure.handler.DefaultCommandHandler;
import com.vuog.telebotmanager.infrastructure.service.CommandRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for creating bot instance handlers
 * Allows different handler types based on bot configuration
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BotHandlerFactory {

    private final BotLifecycleService botLifecycleService;
    private final DefaultCommandHandler defaultCommandHandler;
    private final CommandRouter commandRouter;

    /**
     * Create a bot handler for the given bot
     * Currently returns DefaultBotInstanceHandler, but can be extended
     * to return different handlers based on bot type or configuration
     */
    public BotInstanceHandler createHandler(Bot bot) {
        log.info("Creating bot handler for bot: {}", bot.getBotUsername());

        // For now, always return default handler
        // This can be extended to return different handlers based on:
        // - Bot type (AI, standard, custom)
        // - Bot configuration
        // - Bot capabilities
        return new DefaultBotInstanceHandler(bot, botLifecycleService, defaultCommandHandler, commandRouter);
    }

    /**
     * Create a specific handler type
     * This can be extended to support different handler types
     */
    public BotInstanceHandler createHandler(Bot bot, String handlerType) {
        log.info("Creating {} handler for bot: {}", handlerType, bot.getBotUsername());

        if (handlerType.equalsIgnoreCase("default")) {
            return new DefaultBotInstanceHandler(bot, botLifecycleService, defaultCommandHandler, commandRouter);
            // Add more handler types here as needed
            // case "ai":
            //     return new AiBotInstanceHandler(bot, aiService, commandHandler);
            // case "custom":
            //     return new CustomBotInstanceHandler(bot, customService, commandHandler);
        }
        log.warn("Unknown handler type: {}, using default handler", handlerType);
        return new DefaultBotInstanceHandler(bot, botLifecycleService, defaultCommandHandler, commandRouter);
    }
}
