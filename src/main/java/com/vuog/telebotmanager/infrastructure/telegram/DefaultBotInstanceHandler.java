package com.vuog.telebotmanager.infrastructure.telegram;

import com.vuog.telebotmanager.application.service.BotLifecycleService;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.infrastructure.handler.DefaultCommandHandler;
import com.vuog.telebotmanager.infrastructure.service.CommandRouter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

/**
 * Default implementation of bot instance handler
 * Handles standard bot operations and message processing
 */
@Slf4j
public class DefaultBotInstanceHandler extends BotInstanceHandler {

    private final BotLifecycleService botLifecycleService;
    private final DefaultCommandHandler defaultCommandHandler;
    private final CommandRouter commandRouter;
    /**
     * -- SETTER --
     *  Set the Telegram bot instance for sending messages
     */
    @Setter
    private TelegramBotInstance telegramBotInstance;

    public DefaultBotInstanceHandler(Bot bot, BotLifecycleService botLifecycleService, DefaultCommandHandler defaultCommandHandler, CommandRouter commandRouter) {
        super(bot);
        this.botLifecycleService = botLifecycleService;
        this.defaultCommandHandler = defaultCommandHandler;
        this.commandRouter = commandRouter;
    }

    @Override
    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            String userId = update.getMessage().getFrom().getId().toString();
            String username = update.getMessage().getFrom().getUserName();

            log.info("Bot {} received message from user {} ({}): {}",
                    bot.getBotUsername(), username, userId, messageText);

            try {
                // Process the message
                CommandResponse response = processMessage(userId, chatId, messageText);

                // Send response back to user
                if (telegramBotInstance != null) {
                    telegramBotInstance.sendResponse(chatId, response);
                }

            } catch (Exception e) {
                log.error("Error processing message for bot {}: {}", bot.getBotUsername(), e.getMessage(), e);
                if (telegramBotInstance != null) {
                    telegramBotInstance.sendErrorResponse(chatId, "An error occurred while processing your message.");
                }
            }
        }
    }

    @Override
    public CommandResponse processCommand(CommandRequest request) {
        log.info("Processing command for bot {}: {}", bot.getBotUsername(), request.getCommand());

        try {
            return commandRouter.processCommand(bot, request);
        } catch (Exception e) {
            log.error("Error processing command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), "Command processing failed: " + e.getMessage(), "COMMAND_ERROR");
        }
    }

    /**
     * Process incoming message and return response
     */
    private CommandResponse processMessage(String userId, String chatId, String messageText) {
        log.info("Processing message for bot {}: {}", bot.getBotUsername(), messageText);

        // Create command request
        CommandRequest request = CommandRequest.create(
                UUID.randomUUID().toString(),
                bot.getId().toString(),
                userId,
                chatId,
                extractCommand(messageText),
                messageText
        );

        // Process using command handler
        return processCommand(request);
    }

    /**
     * Extract command from message
     */
    private String extractCommand(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "/unknown";
        }

        String trimmed = message.trim();
        if (trimmed.startsWith("/")) {
            String[] parts = trimmed.split("\\s+");
            return parts[0];
        }

        return "/text";
    }

}
