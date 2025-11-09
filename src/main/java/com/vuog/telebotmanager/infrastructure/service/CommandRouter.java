package com.vuog.telebotmanager.infrastructure.service;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Deterministic command router that tries handlers in a fixed order:
 * Order now based on handler priority (ascending). First available handler that can handle the request will be used.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CommandRouter {

    private final List<CommandHandler> handlers;

    private final CommandRepository commandRepository;

    public CommandResponse processCommand(Bot bot, CommandRequest request) {
        log.info("Processing command for bot {}: {}", bot != null ? bot.getBotUsername() : "<unknown>", request.getCommand());
        try {
            // Try to find command in database first
            Command dbCommand = null;
            Long botId = null;
            try {
                if (request.getBotId() != null) botId = Long.valueOf(request.getBotId());
            } catch (Exception ignored) {
            }
            
            if (botId != null && request.getCommand() != null) {
                // Resolve command from database - prefers bot-specific over global
                List<Command> commands = commandRepository.resolveByBotOrGlobalAndCommand(botId, request.getCommand());
                if (!commands.isEmpty()) {
                    dbCommand = commands.get(0); // Get first match (prioritized by query)
                    log.debug("Found command in DB for {}: {}", request.getCommand(), dbCommand.getId());
                    
                    // If command is disabled, return error
                    if (!dbCommand.getIsEnabled()) {
                        return CommandResponse.error(request.getCommandId(), 
                            "Command is disabled", "COMMAND_DISABLED");
                    }
                }
            }

            // Route by handler priority and capability
            return handlers.stream()
                    .sorted(Comparator.comparingInt(CommandHandler::getPriority))
                    .filter(CommandHandler::isAvailable)
                    .filter(h -> h.canHandle(request))
                    .findFirst()
                    .map(h -> h.execute(request))
                    .orElseGet(() -> CommandResponse.error(request.getCommandId(), 
                        "No handler could process the command", "NO_HANDLER"));
        } catch (Exception e) {
            log.error("Error processing command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), "Command processing failed: " + e.getMessage(), "COMMAND_ERROR");
        }
    }
}
