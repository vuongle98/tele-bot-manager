package com.vuog.telebotmanager.application.service.impl;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.repository.ScheduledMessageRepository;
import com.vuog.telebotmanager.infrastructure.bot.BotHandler;
import com.vuog.telebotmanager.infrastructure.bot.BotRunner;
import com.vuog.telebotmanager.infrastructure.bot.LongPollingBotBase;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSchedulingServiceImpl {
    private final ScheduledMessageRepository messageRepository;
    private final BotRunner botRunner;
    
    @Scheduled(fixedRate = 60000) // Check every minute
    public void processScheduledMessages() {
        LocalDateTime now = LocalDateTime.now();

        messageRepository.findDueMessages(now)
            .forEach(this::sendMessage);
    }
    
    private void sendMessage(ScheduledMessage message) {
        try {
            // Get the bot handler from BotRunner
            BotHandler handler = botRunner.getHandler(message.getBot().getId());
            
            // Send message via appropriate bot type
            if (handler instanceof WebhookBotBase webhookBot) {
                webhookBot.execute(new SendMessage(message.getChatId(), message.getMessageText()));
            } else if (handler instanceof LongPollingBotBase pollingBot) {
                pollingBot.execute(new SendMessage(message.getChatId(), message.getMessageText()));
            }
            
            // Mark as sent or reschedule if recurring
            if (message.getIsRecurring()) {
                message.setScheduledTime(message.getScheduledTime().plus(message.getRecurrenceInterval()));
            } else {
                message.setIsSent(true);
            }
            messageRepository.save(message);
            
        } catch (Exception e) {
            log.error("Failed to send scheduled message {}", message.getId(), e);
        }
    }
}