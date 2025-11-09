package com.vuog.telebotmanager.infrastructure.handler;

import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.service.PluginManager;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Command handler for plugin-based commands
 * Handles PLUGIN command types by delegating to PluginManager
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PluginCommandHandler implements CommandHandler {

    private final PluginManager pluginManager;
    private final CommandRepository commandRepository;

    @Override
    public boolean canHandle(CommandRequest request) {
        // This handler can process plugin commands from the database with type PLUGIN
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
                // Handle PLUGIN type commands
                return command.getType() == Command.CommandType.PLUGIN && command.getIsEnabled();
            }
        }

        return false;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        log.info("Executing plugin command: {}", request.getCommand());

        try {
            // Find command in database to get plugin name
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
            
            // Get plugin name from command
            String pluginName = command.getPluginName();
            if (pluginName == null || pluginName.trim().isEmpty()) {
                return CommandResponse.error(request.getCommandId(), "Plugin name not specified for command", "PLUGIN_NAME_MISSING");
            }

            if (!pluginManager.isPluginLoaded(pluginName)) {
                return CommandResponse.error(request.getCommandId(), "Plugin not loaded: " + pluginName, "PLUGIN_NOT_LOADED");
            }

            // Execute plugin
            CommandResponse response = pluginManager.executePlugin(pluginName, request);

            log.info("Plugin command executed successfully: {}", request.getCommand());
            return response;

        } catch (Exception e) {
            log.error("Error executing plugin command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), "Plugin command execution failed: " + e.getMessage(), "PLUGIN_EXECUTION_ERROR");
        }
    }

    @Override
    public String getSupportedCommandType() {
        return "PLUGIN_COMMAND";
    }

    @Override
    public int getPriority() {
        return 50; // Medium priority for plugin commands
    }

    @Override
    public boolean isAvailable() {
        return pluginManager != null;
    }

    private String extractPluginName(CommandRequest request) {
        // Try to get plugin name from parameters first
        Map<String, Object> parameters = request.getParameters();
        if (parameters != null && parameters.containsKey("pluginName")) {
            return parameters.get("pluginName").toString();
        }

        // Try to extract from command
        String command = request.getCommand();
        if (command != null) {
            String trimmed = command.trim();
            if (trimmed.startsWith("/plugin") || trimmed.startsWith("/custom")) {
                String[] parts = trimmed.split("\\s+");
                if (parts.length > 1) {
                    // Accept /plugin name or /plugin@bot name
                    return parts[1];
                }
            }
        }

        // Try to extract from metadata
        Map<String, Object> metadata = request.getMetadata();
        if (metadata != null && metadata.containsKey("pluginName")) {
            return metadata.get("pluginName").toString();
        }

        return null;
    }
}
