package com.vuog.telebotmanager.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vuog.telebotmanager.application.specification.CommandSpecification;
import com.vuog.telebotmanager.application.usecase.CommandManagementUseCase;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.infrastructure.service.CommandRouter;
import com.vuog.telebotmanager.presentation.dto.query.CommandQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreateCommandRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateCommandRequest;
import com.vuog.telebotmanager.infrastructure.config.AppSettings;
import com.vuog.telebotmanager.application.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing command management use cases
 * Follows Clean Architecture by implementing application layer contracts
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommandManagementService implements CommandManagementUseCase {

    private final CommandRepository commandRepository;
    private final BotRepository botRepository;
    private final List<CommandHandler> commandHandlers;
    private final ObjectMapper objectMapper;
    private final CommandRouter commandRouter;
    private final AppSettings appSettings;
    private final PermissionService permissionService;

    @Override
    public Command createCommand(CreateCommandRequest request) {
        log.info("Creating command: {} for bot: {}", request.getCommand(), request.getBotId());

        if (!permissionService.canCreateCommand(request.getBotId())) {
            throw new SecurityException("User is not allowed to create commands for this bot");
        }

        // Determine default priority based on command type
        int defaultPriority = getDefaultPriorityForType(request.getType());
        
        Command newCommand = Command.builder()
                .command(request.getCommand())
                .description(request.getDescription())
                .type(request.getType())
                .trigger(request.getTrigger())
                .parameters(request.getParameters())
                .responseTemplate(request.getResponseTemplate())
                .pluginName(request.getPluginName())
                .category(getCategoryForType(request.getType()))
                .isEnabled(true)
                .priority(request.getPriority() != null ? request.getPriority() : defaultPriority)
                .timeoutSeconds(request.getTimeoutSeconds() != null ? request.getTimeoutSeconds() : appSettings.getBotDefaults().getTimeoutSeconds())
                .retryCount(request.getRetryCount() != null ? request.getRetryCount() : appSettings.getBotDefaults().getRetryCount())
                .build();

        if (request.getBotId() != null) {
            botRepository.findById(request.getBotId()).ifPresent(newCommand::setBot);
        }

        Command savedCommand = commandRepository.save(newCommand);

        log.info("Command created successfully with ID: {}", savedCommand.getId());
        return savedCommand;
    }

    @Override
    public Command updateCommand(Long commandId, UpdateCommandRequest request) {
        log.info("Updating command: {}", commandId);

        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Command not found with ID: " + commandId));

        command.setDescription(request.getDescription());
        try {
            if (request.getParameters() == null || request.getParameters().isBlank()) {
                command.setParameters(null);
            } else {
                command.setParameters(objectMapper.readTree(request.getParameters()));
            }
        } catch (Exception e) {
            log.warn("Invalid JSON for parameters during update, keeping previous value. value={} error={}", request.getParameters(), e.getMessage());
        }
        command.setAdditionalConfig(request.getAdditionalConfig());
        
        if (request.getResponseTemplate() != null) {
            command.setResponseTemplate(request.getResponseTemplate());
        }
        
        if (request.getPluginName() != null) {
            command.setPluginName(request.getPluginName());
        }
        
        if (request.getPriority() != null) {
            command.setPriority(request.getPriority());
        }

        Command updatedCommand = commandRepository.save(command);

        log.info("Command updated successfully with ID: {}", updatedCommand.getId());
        return updatedCommand;
    }

    @Override
    public Command enableCommand(Long commandId) {
        log.info("Enabling command: {}", commandId);

        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Command not found with ID: " + commandId));

        command.enable();

        Command enabledCommand = commandRepository.save(command);

        log.info("Command enabled successfully with ID: {}", enabledCommand.getId());
        return enabledCommand;
    }

    @Override
    public Command disableCommand(Long commandId) {
        log.info("Disabling command: {}", commandId);

        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Command not found with ID: " + commandId));

        command.disable();

        Command disabledCommand = commandRepository.save(command);

        log.info("Command disabled successfully with ID: {}", disabledCommand.getId());
        return disabledCommand;
    }

    @Override
    public void deleteCommand(Long commandId) {
        log.info("Deleting command: {}", commandId);

        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Command not found with ID: " + commandId));

        commandRepository.delete(command);

        log.info("Command deleted successfully with ID: {}", commandId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Command> getCommandById(Long commandId) {
        return commandRepository.findById(commandId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Command> getCommandsByBotId(Long botId) {
        return commandRepository.findByBotId(botId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Command> getEnabledCommandsByBotId(Long botId) {
        return commandRepository.findEnabledCommandsByBotId(botId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Command> getCommandsByType(Command.CommandType type) {
        return commandRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Command> getAiPoweredCommands() {
        return commandRepository.findAiPoweredCommands();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Command> getPluginCommands() {
        return commandRepository.findPluginCommands();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Command> getCommands(Pageable pageable) {
        return commandRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Command> findAll(CommandQuery query, Pageable pageable) {
        Specification<Command> commandSpecification = CommandSpecification.withFilter(query);
        return commandRepository.findAll(commandSpecification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Command> getCommandsByBotId(Long botId, Pageable pageable) {
        return commandRepository.findByBotId(botId, pageable);
    }

    @Override
    public CommandResponse executeCommand(CommandRequest request) {
        log.info("Executing command: {}", request.getCommand());
        Bot bot = null;
        try {
            if (request.getBotId() != null) {
                bot = botRepository.findById(Long.valueOf(request.getBotId())).orElse(null);
            }
        } catch (Exception ignored) {
        }
        return commandRouter.processCommand(bot, request);
    }

    /**
     * Get default priority based on command type
     */
    private int getDefaultPriorityForType(Command.CommandType type) {
        if (type == null) {
            return 200; // Default for CUSTOM type
        }
        
        return switch (type) {
            case SCHEDULE -> 5;     // High priority for scheduled commands
            case REMINDER -> 10;    // High priority for reminders
            case AI_TASK -> 20;     // AI tasks
            case AI_ANSWER -> 30;   // AI answers
            case SUMMARY -> 40;     // AI summaries
            case GENERATION -> 50;  // AI generation
            case ANALYSIS -> 60;    // AI analysis
            case PLUGIN -> 100;     // Plugin commands
            case CUSTOM -> 200;     // Custom commands
        };
    }

    /**
     * Get category based on command type
     */
    private Command.Category getCategoryForType(Command.CommandType type) {
        if (type == null) {
            return Command.Category.DEFAULT;
        }
        
        return switch (type) {
            case SCHEDULE, REMINDER -> Command.Category.DEFAULT;
            case AI_TASK, AI_ANSWER, SUMMARY, GENERATION, ANALYSIS -> Command.Category.AI;
            case PLUGIN -> Command.Category.PLUGIN;
            case CUSTOM -> Command.Category.DEFAULT;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public CommandStatistics getCommandStatistics(Long commandId) {
        Command command = commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Command not found with ID: " + commandId));

        // Note: CommandExecution statistics would need to be calculated from CommandExecution repository

        return new CommandStatistics() {
            @Override
            public Long getCommandId() {
                return command.getId();
            }

            @Override
            public String getCommand() {
                return command.getCommand();
            }

            @Override
            public long getTotalExecutions() {
                return 0; // Would be calculated from CommandExecution repository
            }

            @Override
            public long getSuccessfulExecutions() {
                return 0; // Would be calculated from CommandExecution repository
            }

            @Override
            public long getFailedExecutions() {
                return 0; // Would be calculated from CommandExecution repository
            }

            @Override
            public double getSuccessRate() {
                return 0.0; // Would be calculated from CommandExecution repository
            }

            @Override
            public long getAverageExecutionTime() {
                return 0; // Would be calculated from CommandExecution repository
            }

            @Override
            public String getLastExecution() {
                return command.getUpdatedAt().toString();
            }

            @Override
            public boolean isEnabled() {
                return command.getIsEnabled();
            }
        };
    }

    private CommandHandler findCommandHandler(CommandRequest request) {
        return commandHandlers.stream()
                .filter(handler -> handler.canHandle(request))
                .filter(CommandHandler::isAvailable)
                .min((h1, h2) -> Integer.compare(h1.getPriority(), h2.getPriority()))
                .orElse(null);
    }
}
