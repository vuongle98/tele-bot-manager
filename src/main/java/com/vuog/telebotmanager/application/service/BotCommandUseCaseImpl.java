package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.command.CreateBotCommandCommand;
import com.vuog.telebotmanager.application.query.BotCommandQuery;
import com.vuog.telebotmanager.application.usecases.BotCommandUseCase;
import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.BotCommandRepository;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
public class BotCommandUseCaseImpl implements BotCommandUseCase {

    private final TelegramBotRepository botRepository;
    private final BotCommandRepository commandRepository;

    @Override
    public BotCommand createCommand(Long botId, CreateBotCommandCommand command) {

        TelegramBot bot = botRepository.findById(botId).orElseThrow(() -> new IllegalArgumentException("Bot not found"));

        BotCommand botCommand = new BotCommand();
        botCommand.setDescription(command.getDescription());
        botCommand.setCommand(command.getCommand());
        botCommand.setResponseTemplate(command.getResponseTemplate());
        botCommand.setBot(bot);
        botCommand.setIsEnabled(true);
        botCommand.setHandlerMethod(command.getHandlerMethod());

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

        if (Objects.nonNull(command.getHandlerMethod())) {
            botCommand.setHandlerMethod(command.getHandlerMethod());
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

            if (query.getHandlerMethod() != null && !query.getHandlerMethod().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("handlerMethod")),
                        "%" + query.getHandlerMethod().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
