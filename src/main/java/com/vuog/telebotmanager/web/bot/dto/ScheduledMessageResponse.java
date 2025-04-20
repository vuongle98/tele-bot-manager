package com.vuog.telebotmanager.web.bot.dto;

import java.time.LocalDateTime;

public record ScheduledMessageResponse(
        Long id,
        String chatId,
        String messagePreview,
        LocalDateTime scheduledTime,
        boolean isRecurring
) {}