package com.vuog.telebotmanager.web.bot.dto;


import java.time.Duration;
import java.time.LocalDateTime;

public record ScheduleMessageRequest(
    String chatId,
    String messageText,
    LocalDateTime scheduledTime,
    Boolean isRecurring,
    Duration recurrenceInterval
) {}