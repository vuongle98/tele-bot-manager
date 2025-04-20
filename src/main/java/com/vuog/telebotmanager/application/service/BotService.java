package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.ScheduledMessageRepository;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.BotRunner;
import com.vuog.telebotmanager.infrastructure.exception.BotNotFoundException;
import com.vuog.telebotmanager.web.bot.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BotService {
    private final TelegramBotRepository botRepository;
    private final BotRunner botRunner;
    private final ScheduledMessageRepository scheduledMessageRepository;

    public TelegramBot createBot(CreateBotRequest request) {
        TelegramBot bot = new TelegramBot();
        bot.setName(request.name());
        bot.setApiToken(request.apiToken());

        BotConfigDto configDTO = request.configuration();
        TelegramBot.BotConfiguration config = new TelegramBot.BotConfiguration();
        config.setUpdateMethod(configDTO.updateMethod());
        config.setWebhookUrl(configDTO.webhookUrl());
        config.setMaxConnections(configDTO.maxConnections());
        config.setAllowedUpdates(configDTO.allowedUpdates());
        config.setIpAddress(configDTO.ipAddress());
        config.setDropPendingUpdates(configDTO.dropPendingUpdates());
        config.setSecretToken(configDTO.secretToken());
        config.setMaxThreads(configDTO.maxThreads());

        bot.setConfiguration(config);

        bot.setOwner(request.owner()); // Set the owner from request
        
        TelegramBot savedBot = botRepository.save(bot);
        log.info("Created new bot with ID: {}", savedBot.getId());
        return savedBot;
    }

    public List<TelegramBot> getAllBots() {
        return botRepository.findAll();
    }

    public Page<TelegramBot> getBotsPageable(Pageable pageable){
        return botRepository.findAll(pageable);
    }

    public void startBot(Long botId) throws TelegramApiException {
        TelegramBot bot = botRepository.findById(botId)
            .orElseThrow(() -> new BotNotFoundException(botId));
        
        if (bot.getStatus() == TelegramBot.BotStatus.RUNNING) {
            throw new IllegalStateException("Bot " + botId + " is already running");
        }
        
        try {
            // Update status before starting
            bot.setStatus(TelegramBot.BotStatus.STARTING);
            botRepository.save(bot);
            
            // Start the bot
            botRunner.startBot(bot);
            
            // Update status after successful start
            bot.setStatus(TelegramBot.BotStatus.RUNNING);
            botRepository.save(bot);
            
            log.info("Successfully started bot with ID: {}", botId);
        } catch (Exception e) {
            bot.setStatus(TelegramBot.BotStatus.ERRORED);
            botRepository.save(bot);
            log.error("Failed to start bot with ID: {}", botId, e);
            throw new TelegramApiException("Failed to start bot: " + e.getMessage(), e);
        }
    }

    public void stopBot(Long botId) {
        TelegramBot bot = botRepository.findById(botId)
            .orElseThrow(() -> new BotNotFoundException(botId));
        
        if (bot.getStatus() != TelegramBot.BotStatus.RUNNING) {
            throw new IllegalStateException("Bot " + botId + " is not running");
        }
        
        try {
            // Update status before stopping
            bot.setStatus(TelegramBot.BotStatus.STOPPING);
            botRepository.save(bot);
            
            // Stop the bot
            botRunner.stopBot(botId);
            
            // Update status after successful stop
            bot.setStatus(TelegramBot.BotStatus.STOPPED);
            botRepository.save(bot);
            
            log.info("Successfully stopped bot with ID: {}", botId);
        } catch (Exception e) {
            bot.setStatus(TelegramBot.BotStatus.ERRORED);
            botRepository.save(bot);
            log.error("Failed to stop bot with ID: {}", botId, e);
            throw new RuntimeException("Failed to stop bot: " + e.getMessage(), e);
        }
    }

    public void deleteBot(Long botId) {
        TelegramBot bot = botRepository.findById(botId)
            .orElseThrow(() -> new BotNotFoundException(botId));
            
        if (bot.getStatus() == TelegramBot.BotStatus.RUNNING) {
            stopBot(botId);
        }
        
        botRepository.delete(bot);
        log.info("Deleted bot with ID: {}", botId);
    }

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
                bot.getOwner().getUsername() // Include owner info
            ))
            .toList();
    }

    public void updateBotConfiguration(Long botId, UpdateBotConfigRequest request) throws TelegramApiException {
        TelegramBot bot = botRepository.findById(botId)
            .orElseThrow(() -> new BotNotFoundException(botId));
        
        boolean wasRunning = bot.getStatus() == TelegramBot.BotStatus.RUNNING;
        
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

    public ScheduledMessage scheduleMessage(Long botId, ScheduleMessageRequest request) {
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

    public List<ScheduledMessage> getScheduledMessages(Long botId) {
        TelegramBot bot = botRepository.findById(botId)
            .orElseThrow(() -> new BotNotFoundException(botId));
        return scheduledMessageRepository.findByBot(bot);
    }

    public void cancelScheduledMessage(Long botId) {
        List<ScheduledMessage> messages = scheduledMessageRepository.findByBotId(botId);
        scheduledMessageRepository.deleteAll(messages);
    }

//    public void cancelScheduledMessage(Long messageId) {
//        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
//                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
//        scheduledMessageRepository.delete(message);
//    }
}
