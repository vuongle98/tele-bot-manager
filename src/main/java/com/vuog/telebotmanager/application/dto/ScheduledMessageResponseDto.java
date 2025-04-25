package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;

import java.time.Duration;
import java.time.LocalDateTime;

public record ScheduledMessageResponseDto(
    Long id,
    Long botId,
    String chatId,
    String messageText,
    LocalDateTime scheduledTime,
    Boolean isRecurring,
    Duration recurrenceInterval,
    Boolean isSent
) {
    public static ScheduledMessageResponseDto fromEntity(ScheduledMessage message) {
        return new ScheduledMessageResponseDto(
            message.getId(),
            message.getBot().getId(), // Only include bot ID, not full object
            message.getChatId(),
            message.getMessageText(),
            message.getScheduledTime(),
            message.getIsRecurring(),
            message.getRecurrenceInterval(),
            message.getIsSent()
        );
    }
}