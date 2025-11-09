package com.vuog.telebotmanager.infrastructure.handler;

import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.infrastructure.util.TelegramUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Command handler for custom commands defined in the database
 * Handles CUSTOM command types by looking up the command in the database
 * and returning the response template or description
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomCommandHandler implements CommandHandler {

    private final CommandRepository commandRepository;

    @Override
    public boolean canHandle(CommandRequest request) {
        // This handler processes custom commands from the database with type CUSTOM
        if (request.getCommand() == null || !request.getCommand().startsWith("/")) {
            return false;
        }

        // Try to find command in database
        Long botId = null;
        try {
            if (request.getBotId() != null) {
                botId = Long.valueOf(request.getBotId());
            }
        } catch (Exception ignored) {
        }

        if (botId != null) {
            List<Command> commands = commandRepository.resolveByBotOrGlobalAndCommand(botId, request.getCommand());
            if (!commands.isEmpty()) {
                Command command = commands.get(0);
                // Handle CUSTOM type commands (with or without response template)
                return command.getType() == Command.CommandType.CUSTOM && command.getIsEnabled();
            }
        }

        return false;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        log.info("Executing custom command: {}", request.getCommand());

        try {
            // Find command in database
            Long botId = null;
            try {
                if (request.getBotId() != null) {
                    botId = Long.valueOf(request.getBotId());
                }
            } catch (Exception ignored) {
            }

            if (botId == null) {
                return CommandResponse.error(request.getCommandId(), "Bot ID is required", "BAD_REQUEST");
            }

            List<Command> commands = commandRepository.resolveByBotOrGlobalAndCommand(botId, request.getCommand());
            if (commands.isEmpty()) {
                return CommandResponse.error(request.getCommandId(), "Command not found", "COMMAND_NOT_FOUND");
            }

            Command command = commands.get(0);

            // Check if command is enabled
            if (!command.getIsEnabled()) {
                return CommandResponse.error(request.getCommandId(), "Command is disabled", "COMMAND_DISABLED");
            }

            // Generate response from template or description
            String responseText = buildResponse(command, request);

            log.info("Custom command executed successfully: {}", request.getCommand());
            return CommandResponse.success(request.getCommandId(), responseText);

        } catch (Exception e) {
            log.error("Error executing custom command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), 
                "Custom command execution failed: " + e.getMessage(), "CUSTOM_COMMAND_ERROR");
        }
    }

    @Override
    public String getSupportedCommandType() {
        return "CUSTOM_COMMAND";
    }

    @Override
    public int getPriority() {
        return 200; // Lower priority than AI and plugins, but higher than default
    }

    @Override
    public boolean isAvailable() {
        return true; // Always available
    }

    /**
     * Build response from command template or description
     */
    private String buildResponse(Command command, CommandRequest request) {
        // If response template is available, use it
        if (command.getResponseTemplate() != null && !command.getResponseTemplate().trim().isEmpty()) {
            String response = command.getResponseTemplate();
            
            // Simple variable substitution
            response = response.replace("{command}", request.getCommand() != null ? request.getCommand() : "");
            response = response.replace("{inputText}", request.getInputText() != null ? request.getInputText() : "");
            response = response.replace("{userId}", request.getUserId() != null ? request.getUserId() : "");
            response = response.replace("{botId}", request.getBotId() != null ? request.getBotId() : "");
            
            // Process the text with Telegram formatting
            return TelegramUtils.formatHtmlText(response);
        }

        // Fallback to description if no template
        if (command.getDescription() != null && !command.getDescription().trim().isEmpty()) {
            return TelegramUtils.formatHtmlText(command.getDescription());
        }

        // Last resort - generic response with input text
        String response = "✅ Command executed: " + request.getCommand();
        if (request.getInputText() != null && !request.getInputText().trim().isEmpty()) {
            response += "\n📝 Input: " + request.getInputText();
        }
        return TelegramUtils.formatHtmlText(response);
    }

    @Override
    public java.util.List<String> getSupportedCommands() {
        // Return empty list as this handler dynamically supports commands from the database
        return java.util.List.of();
    }
}
