package com.vuog.telebotmanager.application.command;


import java.time.Duration;
import java.time.LocalDateTime;

public record ScheduleMessageCommand(
    String chatId,
    String messageText,
    LocalDateTime scheduledTime,
    Boolean isRecurring,
    Duration recurrenceInterval
) {}