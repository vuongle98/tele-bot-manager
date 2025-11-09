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
 * Command handler for reminder-based commands
 * Handles REMINDER command type with reminder functionality
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderCommandHandler implements CommandHandler {

    private final CommandRepository commandRepository;

    @Override
    public boolean canHandle(CommandRequest request) {
        // This handler processes reminder commands from the database
        if (request.getCommand() == null || !request.getCommand().startsWith("/")) {
            return false;
        }

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
                return command.getType() == Command.CommandType.REMINDER && command.getIsEnabled();
            }
        }

        return false;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        log.info("Executing reminder command: {}", request.getCommand());

        try {
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

            if (!command.getIsEnabled()) {
                return CommandResponse.error(request.getCommandId(), "Command is disabled", "COMMAND_DISABLED");
            }

            String inputText = request.getInputText();
            String responseText = processReminderCommand(command, inputText, request);

            log.info("Reminder command executed successfully: {}", request.getCommand());
            return CommandResponse.success(request.getCommandId(), responseText);

        } catch (Exception e) {
            log.error("Error executing reminder command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), 
                "Reminder command execution failed: " + e.getMessage(), "REMINDER_COMMAND_ERROR");
        }
    }

    @Override
    public String getSupportedCommandType() {
        return "REMINDER_COMMAND";
    }

    @Override
    public int getPriority() {
        return 10; // High priority for reminders
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Process reminder command
     */
    private String processReminderCommand(Command command, String inputText, CommandRequest request) {
        if (command.getResponseTemplate() != null && !command.getResponseTemplate().trim().isEmpty()) {
            String response = command.getResponseTemplate();
            response = response.replace("{command}", request.getCommand() != null ? request.getCommand() : "");
            response = response.replace("{inputText}", inputText != null ? inputText : "");
            response = response.replace("{userId}", request.getUserId() != null ? request.getUserId() : "");
            
            // TODO: Parse reminder parameters from input and schedule the reminder
            // For now, return template response
            
            return TelegramUtils.formatHtmlText(response);
        }

        // Default reminder response
        return TelegramUtils.formatHtmlText(
            "⏰ Reminder created!\n\n" +
            "Reminder: " + (inputText != null ? inputText : "Default reminder") + "\n" +
            "Time: " + (command.getTrigger() != null ? command.getTrigger().name() : "MANUAL") + "\n" +
            "Status: Active"
        );
    }

    @Override
    public java.util.List<String> getSupportedCommands() {
        return java.util.List.of();
    }
}
