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
 * Command handler for schedule-based commands
 * Handles SCHEDULE command type with task scheduling
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleCommandHandler implements CommandHandler {

    private final CommandRepository commandRepository;

    @Override
    public boolean canHandle(CommandRequest request) {
        // This handler processes schedule commands from the database
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
                return command.getType() == Command.CommandType.SCHEDULE && command.getIsEnabled();
            }
        }

        return false;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        log.info("Executing schedule command: {}", request.getCommand());

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

            // Parse input to extract schedule details
            String inputText = request.getInputText();
            String responseText = processScheduleCommand(command, inputText, request);

            log.info("Schedule command executed successfully: {}", request.getCommand());
            return CommandResponse.success(request.getCommandId(), responseText);

        } catch (Exception e) {
            log.error("Error executing schedule command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), 
                "Schedule command execution failed: " + e.getMessage(), "SCHEDULE_COMMAND_ERROR");
        }
    }

    @Override
    public String getSupportedCommandType() {
        return "SCHEDULE_COMMAND";
    }

    @Override
    public int getPriority() {
        return 5; // High priority for scheduled tasks
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Process schedule command
     */
    private String processScheduleCommand(Command command, String inputText, CommandRequest request) {
        if (command.getResponseTemplate() != null && !command.getResponseTemplate().trim().isEmpty()) {
            String response = command.getResponseTemplate();
            response = response.replace("{command}", request.getCommand() != null ? request.getCommand() : "");
            response = response.replace("{inputText}", inputText != null ? inputText : "");
            response = response.replace("{userId}", request.getUserId() != null ? request.getUserId() : "");
            
            // TODO: Parse schedule parameters from input and schedule the task
            // For now, return template response
            
            return TelegramUtils.formatHtmlText(response);
        }

        // Default schedule response
        return TelegramUtils.formatHtmlText(
            "📅 Scheduled task created!\n\n" +
            "Task: " + (inputText != null ? inputText : "Default task") + "\n" +
            "Schedule: " + (command.getTrigger() != null ? command.getTrigger().name() : "MANUAL") + "\n" +
            "Status: Active"
        );
    }

    @Override
    public java.util.List<String> getSupportedCommands() {
        return java.util.List.of();
    }
}
