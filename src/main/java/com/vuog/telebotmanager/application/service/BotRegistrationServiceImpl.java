package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.command.CreateBotCommandCommand;
import com.vuog.telebotmanager.application.query.BotCommandQuery;
import com.vuog.telebotmanager.application.usecases.BotCommandRegistrationUseCase;
import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.BotCommandRepository;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.CommandRegistryService;
import com.vuog.telebotmanager.interfaces.dto.query.BotQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service for handling bot registration and initialization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BotRegistrationServiceImpl implements BotCommandRegistrationUseCase {

    private final TelegramBotRepository botRepository;
    private final BotCommandRepository commandRepository;
    private final CommandRegistryService commandRegistryService;

    /**
     * Creates default commands for a newly registered bot
     * @param bot The newly created bot
     */
    @Transactional
    public void createDefaultCommandsForBot(TelegramBot bot) {
        log.info("Creating default commands for new bot: {} (ID: {})", bot.getName(), bot.getId());
        List<BotCommand> defaultCommands = createDefaultCommands(bot);
        commandRepository.saveAll(defaultCommands);
        log.info("Added {} default commands for bot: {}", defaultCommands.size(), bot.getName());
        
        // Register new commands with the command registry
        commandRegistryService.registerCommands(defaultCommands);
    }

    /**
     * Creates a list of default commands for a bot
     */
    private List<BotCommand> createDefaultCommands(TelegramBot bot) {
        List<BotCommand> commands = new ArrayList<>();

        // Weather command
        BotCommand weatherCommand = new BotCommand();
        weatherCommand.setBot(bot);
        weatherCommand.setCommand("/weather");
        weatherCommand.setDescription("Get weather information for a location");
        weatherCommand.setResponseTemplate("Weather information for {{args}}:\n\n🌡️ Temperature: 25°C\n💨 Wind: 5 km/h\n💧 Humidity: 65%\n\nNote: This is a demo response. Integrate with a weather API for real data.");
        weatherCommand.setIsEnabled(true);
        commands.add(weatherCommand);

        // Quote command
        BotCommand quoteCommand = new BotCommand();
        quoteCommand.setBot(bot);
        quoteCommand.setCommand("/quote");
        quoteCommand.setDescription("Get a random inspirational quote");
        quoteCommand.setResponseTemplate("\"The only way to do great work is to love what you do.\" — Steve Jobs\n\nRequested by: {{firstName}} {{lastName}}");
        quoteCommand.setIsEnabled(true);
        commands.add(quoteCommand);

        // Echo command
        BotCommand echoCommand = new BotCommand();
        echoCommand.setBot(bot);
        echoCommand.setCommand("/echo");
        echoCommand.setDescription("Echo back your message");
        echoCommand.setResponseTemplate("You said: {{args}}");
        echoCommand.setIsEnabled(true);
        commands.add(echoCommand);

        // Joke command
        BotCommand jokeCommand = new BotCommand();
        jokeCommand.setBot(bot);
        jokeCommand.setCommand("/joke");
        jokeCommand.setDescription("Get a random joke");
        jokeCommand.setResponseTemplate("Why don't scientists trust atoms?\nBecause they make up everything! 😄");
        jokeCommand.setIsEnabled(true);
        commands.add(jokeCommand);

        // Custom greeting command
        BotCommand greetingCommand = new BotCommand();
        greetingCommand.setBot(bot);
        greetingCommand.setCommand("/hello");
        greetingCommand.setDescription("Get a personalized greeting");
        greetingCommand.setResponseTemplate("Hello, {{firstName}}! Welcome to {{botName}}. How can I help you today?");
        greetingCommand.setIsEnabled(true);
        commands.add(greetingCommand);

        return commands;
    }

    @Override
    public BotCommand createCommand(Long botId, CreateBotCommandCommand command) {

        TelegramBot bot = botRepository.findById(botId).orElseThrow(() -> new IllegalArgumentException("Bot not found"));

        BotCommand botCommand = new BotCommand();
        botCommand.setDescription(command.getDescription());
        botCommand.setCommand(command.getCommand());
        botCommand.setResponseTemplate(command.getResponseTemplate());
        botCommand.setBot(bot);
        botCommand.setIsEnabled(true);

        return commandRepository.save(botCommand);
    }

    @Override
    public void deleteCommand(Long botId, String commandName) {
        Optional<BotCommand> command = commandRepository.findAllByIsEnabledTrueAndBotIdAndCommand(botId, commandName);

        if (command.isPresent()) {
            BotCommand botCommand = command.get();
            botCommand.setIsEnabled(false);
            commandRepository.save(botCommand);
            log.info("Disabled command {} for bot {}", commandName, botId);
        } else {
            log.warn("Command {} not found for bot {}", commandName, botId);
        }
    }

    @Override
    public void deleteAllCommands(Long botId) {
        List<BotCommand> commands = commandRepository.findAllByIsEnabledTrueAndBotId(botId);

        for (BotCommand command : commands) {
            command.setIsEnabled(false);
            commandRepository.save(command);
        }

        log.info("Disabled all commands for bot {}", botId);
    }

    @Override
    public BotCommand getCommand(Long botId, String commandName) {
        Optional<BotCommand> command = commandRepository.findAllByIsEnabledTrueAndBotIdAndCommand(botId, commandName);
        return command.orElseThrow(() -> new IllegalArgumentException("Command not found"));
    }

    @Override
    public BotCommand updateCommand(Long botId, String commandName, CreateBotCommandCommand command) {

        BotCommand botCommand = getCommand(botId, commandName);

        if (Objects.nonNull(command.getDescription())) {
            botCommand.setDescription(command.getDescription());
        }

        if (Objects.nonNull(command.getCommand())) {
            botCommand.setCommand(command.getCommand());
        }

        if (Objects.nonNull(command.getResponseTemplate())) {
            botCommand.setResponseTemplate(command.getResponseTemplate());
        }

        if (Objects.nonNull(command.getAdditionalConfig())) {
            botCommand.setAdditionalConfig(command.getAdditionalConfig());
        }

        return commandRepository.save(botCommand);
    }

    @Override
    public List<BotCommand> getAllCommands(Long botId) {
        return commandRepository.findAllByIsEnabledTrueAndBotId(botId);
    }

    @Override
    public Page<BotCommand> getAllCommands(Long botId, BotCommandQuery query, Pageable pageable) {
        Specification<BotCommand> spec = buildBotSpecification(query);

        return commandRepository.findAll(spec, pageable);
    }

    /**
     * Builds a specification for filtering bots based on query parameters
     *
     * @param query The query parameters
     * @return Specification for filtering
     */
    private Specification<BotCommand> buildBotSpecification(BotCommandQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by command
            if (query.getCommand() != null && !query.getCommand().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + query.getCommand().toLowerCase() + "%"
                ));
            }

            // filter by description
            if (query.getDescription() != null && !query.getDescription().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + query.getDescription().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
