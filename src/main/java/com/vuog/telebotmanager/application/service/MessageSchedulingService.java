package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.repository.ScheduledMessageRepository;
import com.vuog.telebotmanager.domain.bot.ports.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSchedulingService {
    private final ScheduledMessageRepository messageRepository;
    private final TelegramMessageSender messageSender;
    
    @Scheduled(fixedRate = 60000) // Check every minute
    public void processScheduledMessages() {
        LocalDateTime now = LocalDateTime.now();
        messageRepository.findAllByIsSentFalseAndIsCancelledFalseAndIsCancelledFalseAndScheduledTimeLessThanEqual(now)
            .forEach(this::processMessage);
    }
    
    private void processMessage(ScheduledMessage message) {
        try {

            // Send message through the port
            boolean sent = messageSender.sendMessage(
                message.getBot().getId(), 
                message.getChatId(), 
                message.getMessageText()
            );
            
            if (sent) {
                // If this is a recurring message, schedule the next occurrence
                if (Boolean.TRUE.equals(message.getIsRecurring()) && message.getRecurrenceInterval() != null) {
                    createNextRecurrence(message);
                }

                // Update the current message as sent
                message.setIsSent(true);
                message.setSentAt(LocalDateTime.now());

                messageRepository.save(message);
            }
        } catch (Exception e) {
            log.error("Failed to send scheduled message {}", message.getId(), e);
        }
    }

    /**
     * Create the next occurrence of a recurring message
     */
    private void createNextRecurrence(ScheduledMessage currentMessage) {
        ScheduledMessage nextMessage = new ScheduledMessage();
        nextMessage.setBot(currentMessage.getBot());
        nextMessage.setChatId(currentMessage.getChatId());
        nextMessage.setMessageText(currentMessage.getMessageText());

        // Calculate next occurrence time based on the recurrence interval
        LocalDateTime nextScheduledTime = currentMessage.getScheduledTime().plus(
                currentMessage.getRecurrenceInterval().toMillis(), ChronoUnit.MILLIS);

        nextMessage.setScheduledTime(nextScheduledTime);
        nextMessage.setIsRecurring(true);
        nextMessage.setRecurrenceInterval(currentMessage.getRecurrenceInterval());
        nextMessage.setIsSent(false);
        nextMessage.setIsCancelled(false);

        messageRepository.save(nextMessage);
        log.info("Created next recurrence of message ID: {} scheduled for {}",
                currentMessage.getId(), nextScheduledTime);
    }
}