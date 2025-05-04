package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import com.vuog.telebotmanager.domain.bot.repository.BotCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Service to handle command registration with the CommandRegistry
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommandRegistryService {

    private final CommandRegistry commandRegistry;
    private final BotCommandRepository botCommandRepository;
    private final DefaultCommandHandler defaultCommandHandler;

    /**
     * Initialize the command registry with existing commands on startup
     */
    @PostConstruct
    public void initializeRegistry() {
        List<BotCommand> allCommands = botCommandRepository.findAll();
        registerCommands(allCommands);
        log.info("Initialized command registry with {} commands", allCommands.size());
    }

    /**
     * Register a list of commands to the command registry
     * 
     * @param commands The list of commands to register
     */
    public void registerCommands(List<BotCommand> commands) {
        for (BotCommand command : commands) {
            registerSingleCommand(command);
        }
    }

    /**
     * Register a single command to the command registry
     * 
     * @param botCommand The command to register
     */
    public void registerSingleCommand(BotCommand botCommand) {
        if (botCommand.getIsEnabled() && !commandRegistry.isRegistered(generateCommandKey(botCommand))) {
            String commandKey = generateCommandKey(botCommand);
            commandRegistry.registerCommand(commandKey, context -> defaultCommandHandler.handleCommand(botCommand, context));
            log.debug("Registered command: {} for bot ID: {}", botCommand.getCommand(), botCommand.getBot().getId());
        } else {
            log.debug("Skipping disabled command: {} for bot ID: {}", botCommand.getCommand(), botCommand.getBot().getId());
        }
    }

    /**
     * Generate a unique key for each command in the registry
     * Format: botId:command
     * 
     * @param botCommand The command to generate a key for
     * @return The unique key for the command
     */
    private String generateCommandKey(BotCommand botCommand) {
        return botCommand.getBot().getId() + ":" + botCommand.getCommand();
    }
}
