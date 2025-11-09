package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.specification.BotSpecification;
import com.vuog.telebotmanager.application.usecase.BotManagementUseCase;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotHistory;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.BotHistoryRepository;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.infrastructure.command.DefaultBotCommands;
import com.vuog.telebotmanager.presentation.dto.query.BotQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreateBotRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateBotRequest;
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
 * Application service implementing bot management use cases
 * Follows Clean Architecture by implementing application layer contracts
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BotManagementService implements BotManagementUseCase {

    private final BotRepository botRepository;
    private final CommandRepository commandRepository;
    private final BotHistoryRepository botHistoryRepository;
    private final List<CommandHandler> commandHandlers;
    private final DefaultBotCommands defaultBotCommands;

    @Override
    public Bot createBot(CreateBotRequest request) {
        log.info("Creating bot with username: {}", request.getBotUsername());

        // Validate bot doesn't already exist
        if (botRepository.existsByBotToken(request.getBotToken())) {
            throw new IllegalArgumentException("Bot with token already exists");
        }
        if (botRepository.existsByBotUsername(request.getBotUsername())) {
            throw new IllegalArgumentException("Bot with username already exists");
        }

        Bot bot = Bot.builder()
                .botToken(request.getBotToken())
                .botUsername(request.getBotUsername())
                .botName(request.getBotName())
                .webhookUrl(request.getWebhookUrl())
                .status(Bot.BotStatus.INACTIVE)
                .isActive(false)
                .build();

        Bot savedBot = botRepository.save(bot);

        // Create default commands for the bot
        defaultBotCommands.createDefaultCommands(savedBot.getId());

        // Create AI commands if AI is enabled
        try {
            defaultBotCommands.createAiCommands(savedBot.getId());
        } catch (Exception e) {
            log.warn("Could not create AI commands for bot {}: {}", savedBot.getId(), e.getMessage());
        }

        // Create history record
        BotHistory history = BotHistory.createStatusChange(
                savedBot, Bot.BotStatus.INACTIVE, Bot.BotStatus.INACTIVE, null, "Bot created");
        botHistoryRepository.save(history);

        log.info("Bot created successfully with ID: {}", savedBot.getId());
        return savedBot;
    }

    @Override
    public Bot updateBot(Long botId, UpdateBotRequest request) {
        log.info("Updating bot with ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        Bot.BotStatus previousStatus = bot.getStatus();

        bot.setBotName(request.getBotName());
        bot.setDescription(request.getDescription());
        bot.setWebhookUrl(request.getWebhookUrl());

        Bot updatedBot = botRepository.save(bot);

        // Create history record if status changed
        if (!previousStatus.equals(updatedBot.getStatus())) {
            BotHistory history = BotHistory.createStatusChange(
                    updatedBot, previousStatus, updatedBot.getStatus(), null, "Bot updated");
            botHistoryRepository.save(history);
        }

        log.info("Bot updated successfully with ID: {}", updatedBot.getId());
        return updatedBot;
    }

    @Override
    public Bot activateBot(Long botId) {
        log.info("Activating bot with ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        Bot.BotStatus previousStatus = bot.getStatus();
        bot.activate();

        Bot activatedBot = botRepository.save(bot);

        // Create history record
        BotHistory history = BotHistory.createStatusChange(
                activatedBot, previousStatus, Bot.BotStatus.ACTIVE, null, "Bot activated");
        botHistoryRepository.save(history);

        log.info("Bot activated successfully with ID: {}", activatedBot.getId());
        return activatedBot;
    }

    @Override
    public Bot deactivateBot(Long botId) {
        log.info("Deactivating bot with ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        Bot.BotStatus previousStatus = bot.getStatus();
        bot.deactivate();

        Bot deactivatedBot = botRepository.save(bot);

        // Create history record
        BotHistory history = BotHistory.createStatusChange(
                deactivatedBot, previousStatus, Bot.BotStatus.INACTIVE, null, "Bot deactivated");
        botHistoryRepository.save(history);

        log.info("Bot deactivated successfully with ID: {}", deactivatedBot.getId());
        return deactivatedBot;
    }

    @Override
    public void deleteBot(Long botId) {
        log.info("Deleting bot with ID: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        // Create history record before deletion
        BotHistory history = BotHistory.createStatusChange(
                bot, bot.getStatus(), null, null, "Bot deleted");
        botHistoryRepository.save(history);

        botRepository.delete(bot);

        log.info("Bot deleted successfully with ID: {}", botId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bot> getBotById(Long botId) {
        return botRepository.findById(botId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bot> getBotByToken(String botToken) {
        return botRepository.findByBotToken(botToken);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bot> getBotByUsername(String botUsername) {
        return botRepository.findByBotUsername(botUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bot> getAllBots(Pageable pageable) {
        return botRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bot> findAll(BotQuery query, Pageable pageable) {
        Specification<Bot> botSpecification = BotSpecification.withFilter(query);
        return botRepository.findAll(botSpecification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bot> getActiveBots() {
        return botRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bot> getOperationalBots() {
        return botRepository.findOperationalBots();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bot> getBotsByStatus(Bot.BotStatus status) {
        return botRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Bot> getBotsByUser(String createdBy, Pageable pageable) {
        return botRepository.findByCreatedBy(createdBy, pageable);
    }

    @Override
    public CommandResponse processMessage(CommandRequest request) {
        log.info("Processing message for bot: {}, command: {}", request.getBotId(), request.getCommand());

        // Find the bot
        Bot bot = botRepository.findById(Long.valueOf(request.getBotId()))
                .orElseThrow(() -> new IllegalArgumentException("Bot not found"));

        if (!bot.isOperational()) {
            return CommandResponse.error(request.getCommandId(), "Bot is not operational", "BOT_NOT_OPERATIONAL");
        }

        // Find matching command
        Optional<Command> commandOpt = commandRepository.findByBotIdAndCommand(
                Long.valueOf(request.getBotId()), request.getCommand());

        if (commandOpt.isEmpty()) {
            return CommandResponse.error(request.getCommandId(), "Command not found", "COMMAND_NOT_FOUND");
        }

        Command command = commandOpt.get();
        if (!command.isExecutable()) {
            return CommandResponse.error(request.getCommandId(), "Command is not executable", "COMMAND_NOT_EXECUTABLE");
        }

        // Find appropriate handler
        CommandHandler handler = findCommandHandler(request, command);
        if (handler == null) {
            return CommandResponse.error(request.getCommandId(), "No handler found for command", "NO_HANDLER_FOUND");
        }

        // Execute command
        try {
            CommandResponse response = handler.execute(request);
            log.info("Command executed successfully for bot: {}, command: {}", request.getBotId(), request.getCommand());
            return response;
        } catch (Exception e) {
            log.error("Error executing command for bot: {}, command: {}", request.getBotId(), request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), "Command execution failed: " + e.getMessage(), "EXECUTION_ERROR");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BotStatistics getBotStatistics(Long botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        List<Command> commands = commandRepository.findByBotId(botId);
        long totalCommands = commands.size();
        long activeCommands = commands.stream().mapToLong(c -> c.getIsEnabled() ? 1 : 0).sum();

        // Note: CommandExecution statistics would need to be calculated from CommandExecution repository

        return new BotStatistics() {
            @Override
            public Long getBotId() {
                return bot.getId();
            }

            @Override
            public String getBotUsername() {
                return bot.getBotUsername();
            }

            @Override
            public long getTotalCommands() {
                return totalCommands;
            }

            @Override
            public long getActiveCommands() {
                return activeCommands;
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
            public String getLastActivity() {
                return bot.getUpdatedAt().toString();
            }
        };
    }

    @Override
    @Transactional(readOnly = true)
    public AllBotsStatistics getAllBotsStatistics() {
        List<Bot> allBots = botRepository.findAll();
        long totalBots = allBots.size();
        long activeBots = allBots.stream().mapToLong(b -> b.getIsActive() ? 1 : 0).sum();
        
        List<Command> allCommands = commandRepository.findAll();
        long totalCommands = allCommands.size();
        long activeCommands = allCommands.stream().mapToLong(c -> c.getIsEnabled() ? 1 : 0).sum();
        
        // Note: Plugin statistics would need to be calculated from Plugin repository
        long totalPlugins = 0;
        long activePlugins = 0;

        return new AllBotsStatistics() {
            @Override
            public long getTotalBots() {
                return totalBots;
            }

            @Override
            public long getActiveBots() {
                return activeBots;
            }

            @Override
            public long getTotalCommands() {
                return totalCommands;
            }

            @Override
            public long getActiveCommands() {
                return activeCommands;
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
            public double getAverageSuccessRate() {
                return 0.0; // Would be calculated from CommandExecution repository
            }

            @Override
            public long getTotalPlugins() {
                return totalPlugins;
            }

            @Override
            public long getActivePlugins() {
                return activePlugins;
            }
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotHistory> getBotHistory(Long botId) {
        return botHistoryRepository.findByBotIdOrderByTimestampDesc(botId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BotHistory> getBotHistory(Long botId, Pageable pageable) {
        return botHistoryRepository.findByBotIdOrderByTimestampDesc(botId, pageable);
    }

    private CommandHandler findCommandHandler(CommandRequest request, Command command) {
        return commandHandlers.stream()
                .filter(handler -> handler.canHandle(request))
                .filter(CommandHandler::isAvailable)
                .min((h1, h2) -> Integer.compare(h1.getPriority(), h2.getPriority()))
                .orElse(null);
    }
}
