package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.command.CreateBotCommand;
import com.vuog.telebotmanager.application.command.ScheduleMessageCommand;
import com.vuog.telebotmanager.application.command.UpdateBotConfigCommand;
import com.vuog.telebotmanager.application.dto.BotConfigDto;
import com.vuog.telebotmanager.application.dto.BotResponseDto;
import com.vuog.telebotmanager.application.dto.BotStatusResponse;
import com.vuog.telebotmanager.application.usecases.BotHistoryUseCase;
import com.vuog.telebotmanager.application.usecases.BotUseCase;
import com.vuog.telebotmanager.application.usecases.ScheduleMessageUseCase;
import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.common.exception.BotNotFoundException;
import com.vuog.telebotmanager.common.exception.ResourceNotFoundException;
import com.vuog.telebotmanager.domain.bot.model.BotHistory;
import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.BotHistoryRepository;
import com.vuog.telebotmanager.domain.bot.repository.ScheduledMessageRepository;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.BotRunner;
import com.vuog.telebotmanager.interfaces.dto.query.BotQuery;
import com.vuog.telebotmanager.interfaces.dto.request.UpdateBotRequest;
import com.vuog.telebotmanager.interfaces.dto.response.BotDetailResponseDto;
import com.vuog.telebotmanager.interfaces.dto.response.BotHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BotUseCaseImpl implements BotUseCase, ScheduleMessageUseCase, BotHistoryUseCase {
    private final TelegramBotRepository botRepository;
    private final BotRunner botRunner;
    private final ScheduledMessageRepository scheduledMessageRepository;
    private final BotHistoryRepository botHistoryRepository;


    @Override
    public TelegramBot createBot(CreateBotCommand request) {
        TelegramBot bot = new TelegramBot();
        bot.setName(request.name());
        bot.setApiToken(request.apiToken());

        BotConfigDto configDTO = request.configuration();
        TelegramBot.BotConfiguration config = new TelegramBot.BotConfiguration();
        config.setUpdateMethod(configDTO.updateMethod());
        config.setWebhookUrl(configDTO.webhookUrl());
        config.setMaxConnections(configDTO.maxConnections());
        config.setAllowedUpdates(configDTO.allowedUpdates());
//        config.setIpAddress(configDTO.ipAddress());
//        config.setDropPendingUpdates(configDTO.dropPendingUpdates());
//        config.setSecretToken(configDTO.secretToken());
//        config.setMaxThreads(configDTO.maxThreads());

        bot.setConfiguration(config);

//        bot.setOwner(request.owner()); // Set the owner from request

        TelegramBot savedBot = botRepository.save(bot);
        log.info("Created new bot with ID: {}", savedBot.getId());
        return savedBot;
    }

    /**
     * Get all bots with pagination and filtering
     *
     * @param query The query parameters for filtering bots
     * @param pageable Pagination information
     * @return Page of BotResponseDto
     */
    public Page<BotResponseDto> getBots(BotQuery query, Pageable pageable) {
        Specification<TelegramBot> spec = buildBotSpecification(query);
        Page<TelegramBot> bots = botRepository.findAll(spec, pageable);
        return bots.map(BotResponseDto::fromEntity);
    }

    /**
     * Builds a specification for filtering bots based on query parameters
     *
     * @param query The query parameters
     * @return Specification for filtering
     */
    private Specification<TelegramBot> buildBotSpecification(BotQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name (case insensitive, partial match)
            if (query.getName() != null && !query.getName().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + query.getName().toLowerCase() + "%"
                ));
            }

            // Filter by status
            if (query.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), query.getStatus()));
            }

            // Filter by update method
            if (query.getUpdateMethod() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("configuration").get("updateMethod"),
                    query.getUpdateMethod()
                ));
            }

            // Filter by owner ID
            if (query.getOwnerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), query.getOwnerId()));
            }

            // Filter by creation date range
            if (query.getCreatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    query.getCreatedAfter()
                ));
            }

            if (query.getCreatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    query.getCreatedBefore()
                ));
            }

            // Filter by scheduled status
            if (query.getScheduled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("scheduled"), query.getScheduled()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Get all bots without pagination
     *
     * @return List of TelegramBot entities
     */
    public List<TelegramBot> getAllBots() {
        return botRepository.findAll();
    }

    /**
     * Get detailed information about a specific bot
     *
     * @param id The ID of the bot
     * @return BotDetailResponseDto with detailed information
     */
    public BotDetailResponseDto getBotDetails(Long id) {
        TelegramBot bot = botRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found with ID: " + id));
        return BotDetailResponseDto.fromEntity(bot);
    }

    /**
     * Update bot information
     *
     * @param id      The ID of the bot to update
     * @param request The update request data
     * @return Updated BotResponseDto
     */
    @Transactional
    public BotResponseDto updateBot(Long id, UpdateBotRequest request) {
        TelegramBot bot = botRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found with ID: " + id));

        // Update basic bot information
        if (request.getName() != null) {
            bot.setName(request.getName());
        }

        if (request.getApiToken() != null) {
            bot.setApiToken(request.getApiToken());
        }

        if (request.getScheduled() != null) {
            bot.setScheduled(request.getScheduled());
        }

        // Update configuration if provided
        if (request.getConfiguration() != null) {
            UpdateBotRequest.ConfigurationRequest configRequest = request.getConfiguration();
            TelegramBot.BotConfiguration config = bot.getConfiguration();

            if (config == null) {
                config = new TelegramBot.BotConfiguration();
                bot.setConfiguration(config);
            }

            if (configRequest.getUpdateMethod() != null) {
                config.setUpdateMethod(configRequest.getUpdateMethod());
            }

            if (configRequest.getWebhookUrl() != null) {
                config.setWebhookUrl(configRequest.getWebhookUrl());
            }

            if (configRequest.getMaxConnections() != null) {
                config.setMaxConnections(configRequest.getMaxConnections());
            }

            if (configRequest.getAllowedUpdates() != null) {
                config.setAllowedUpdates(configRequest.getAllowedUpdates());
            }

            if (configRequest.getIsWebhookEnabled() != null) {
                config.setWebhookEnabled(configRequest.getIsWebhookEnabled());
            }

            if (configRequest.getPollingTimeout() != null) {
                config.setPollingTimeout(configRequest.getPollingTimeout());
            }

            if (configRequest.getPollingLimit() != null) {
                config.setPollingLimit(configRequest.getPollingLimit());
            }
        }

        TelegramBot updatedBot = botRepository.save(bot);
        return BotResponseDto.fromEntity(updatedBot);
    }

    /**
     * Refresh the status of a bot
     *
     * @param id The ID of the bot
     */
    public void refreshBotStatus(Long id) {
        TelegramBot bot = botRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found with ID: " + id));

        // Here you would typically check the actual status of the bot
        // For now, we'll just log the action
        log.info("Refreshing status for bot: {} (ID: {})", bot.getName(), id);

        // This would be where you'd check if the bot is actually running
        // For demonstration, we'll record the current status in history
        recordBotStatusChange(bot, bot.getStatus(), bot.getStatus(), "Status refresh requested by user");
    }

    /**
     * Get the status history for a bot
     *
     * @param id       The ID of the bot
     * @param pageable Pagination information
     * @return Page of BotHistoryResponseDto
     */
    public Page<BotHistoryResponseDto> getAllByBot(Long id, Pageable pageable) {
        // Check if bot exists
        if (!botRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bot not found with ID: " + id);
        }

        Page<BotHistory> history = botHistoryRepository.findByBotIdOrderByTimestampDesc(id, pageable);
        return history.map(BotHistoryResponseDto::fromEntity);
    }

    /**
     * Get the status history
     *
     * @param pageable Pagination information
     * @return Page of BotHistoryResponseDto
     */
    public Page<BotHistoryResponseDto> getAll(Pageable pageable) {
        Page<BotHistory> history = botHistoryRepository.findAll(pageable);
        return history.map(BotHistoryResponseDto::fromEntity);
    }

    /**
     * Record a change in bot status
     *
     * @param bot            The bot entity
     * @param previousStatus The previous status
     * @param newStatus      The new status
     * @param notes          Additional notes about the status change
     */
    @Transactional
    public void recordBotStatusChange(TelegramBot bot, CommonEnum.BotStatus previousStatus,
                                      CommonEnum.BotStatus newStatus, String notes) {
        BotHistory history = BotHistory.builder()
                .bot(bot)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .notes(notes)
                .timestamp(LocalDateTime.now())
                .build();

        botHistoryRepository.save(history);
    }

    /**
     * Record a bot error with details
     *
     * @param bot            The bot entity
     * @param previousStatus The previous status
     * @param errorMessage   The error message
     */
    @Transactional
    public void recordBotError(TelegramBot bot, CommonEnum.BotStatus previousStatus, String errorMessage) {
        BotHistory history = BotHistory.builder()
                .bot(bot)
                .previousStatus(previousStatus)
                .newStatus(CommonEnum.BotStatus.ERRORED)
                .notes("Bot encountered an error")
                .errorDetails(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();

        botHistoryRepository.save(history);

        // Update bot status
        bot.setStatus(CommonEnum.BotStatus.ERRORED);
        botRepository.save(bot);
    }

    public Page<TelegramBot> getBotsPageable(Pageable pageable) {
        return botRepository.findAll(pageable);
    }

    public void startBot(Long botId) throws TelegramApiException {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        if (bot.getStatus() == CommonEnum.BotStatus.RUNNING) {
            throw new IllegalStateException("Bot " + botId + " is already running");
        }

        try {
            // Update status before starting
            bot.setStatus(CommonEnum.BotStatus.STARTING);
            botRepository.save(bot);

            // Start the bot
            botRunner.startBot(bot);

            // Update status after successful start
            bot.setStatus(CommonEnum.BotStatus.RUNNING);
            botRepository.save(bot);

            log.info("Successfully started bot with ID: {}", botId);
        } catch (Exception e) {
            bot.setStatus(CommonEnum.BotStatus.ERRORED);
            botRepository.save(bot);
            log.error("Failed to start bot with ID: {}", botId, e);
            throw new TelegramApiException("Failed to start bot: " + e.getMessage(), e);
        }
    }

    public void stopBot(Long botId) {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        if (bot.getStatus() != CommonEnum.BotStatus.RUNNING) {
            throw new IllegalStateException("Bot " + botId + " is not running");
        }

        try {
            // Update status before stopping
            bot.setStatus(CommonEnum.BotStatus.STOPPING);
            botRepository.save(bot);

            // Stop the bot
            botRunner.stopBot(botId);

            // Update status after successful stop
            bot.setStatus(CommonEnum.BotStatus.STOPPED);
            botRepository.save(bot);

            log.info("Successfully stopped bot with ID: {}", botId);
        } catch (Exception e) {
            bot.setStatus(CommonEnum.BotStatus.ERRORED);
            botRepository.save(bot);
            log.error("Failed to stop bot with ID: {}", botId, e);
            throw new RuntimeException("Failed to stop bot: " + e.getMessage(), e);
        }
    }

    public void deleteBot(Long botId) {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        if (bot.getStatus() == CommonEnum.BotStatus.RUNNING) {
            stopBot(botId);
        }

        botRepository.delete(bot);
        log.info("Deleted bot with ID: {}", botId);
    }

    @Override
    public void restartBot(Long botId) throws TelegramApiException {
        stopBot(botId);
        startBot(botId);
        log.info("Restarted bot with ID: {}", botId);
    }

    public List<BotStatusResponse> getAllBotStatuses() {
        return botRepository.findAll().stream()
                .map(bot -> new BotStatusResponse(
                        bot.getId(),
                        bot.getName(),
                        bot.getStatus(),
                        bot.getConfiguration().getUpdateMethod(),
                        bot.getOwner() != null ? bot.getOwner().getUsername() : null
                ))
                .toList();
    }

    public void updateBotConfiguration(Long botId, UpdateBotConfigCommand request) throws TelegramApiException {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        boolean wasRunning = bot.getStatus() == CommonEnum.BotStatus.RUNNING;

        if (wasRunning) {
            stopBot(botId);
        }

        bot.setConfiguration(request.configuration());
        botRepository.save(bot);

        if (wasRunning && request.restartOnUpdate()) {
            startBot(botId);
        }

        log.info("Updated configuration for bot with ID: {}", botId);
    }

    @Override
    public ScheduledMessage scheduleMessage(Long botId, ScheduleMessageCommand request) {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        ScheduledMessage message = new ScheduledMessage();
        message.setBot(bot);
        message.setChatId(request.chatId());
        message.setMessageText(request.messageText());
        message.setScheduledTime(request.scheduledTime());
        message.setIsRecurring(request.isRecurring());
        message.setRecurrenceInterval(request.recurrenceInterval());
        message.setIsSent(false);

        return scheduledMessageRepository.save(message);
    }

    public List<ScheduledMessage> getScheduledMessagesForBot(Long botId) {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));
        return scheduledMessageRepository.findByBot(bot);
    }

    public void cancelAllScheduledMessageByBot(Long botId) {
        List<ScheduledMessage> messages = scheduledMessageRepository.findByBotId(botId);

        messages.forEach(message -> {
            message.setIsCancelled(true);

            scheduledMessageRepository.save(message);
        });
//        scheduledMessageRepository.saveAll(messages);
    }

    public void cancelScheduledMessage(Long messageId) {
        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.setIsCancelled(true);
        scheduledMessageRepository.save(message);
    }
}
