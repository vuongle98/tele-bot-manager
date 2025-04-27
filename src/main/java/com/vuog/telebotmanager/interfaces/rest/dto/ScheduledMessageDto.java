package com.vuog.telebotmanager.interfaces.rest.dto;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class ScheduledMessageDto {
    private Long id;
    private Long botId;
    private String chatId;
    private String messageText;
    private LocalDateTime scheduledTime;
    private Boolean isRecurring;
    private Duration recurrenceInterval;
    private Boolean isSent;

    public static ScheduledMessageDto fromEntity(ScheduledMessage entity) {
        ScheduledMessageDto dto = new ScheduledMessageDto();
        dto.setId(entity.getId());
        dto.setBotId(entity.getBot().getId());
        dto.setChatId(entity.getChatId());
        dto.setMessageText(entity.getMessageText());
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setIsRecurring(entity.getIsRecurring());
        dto.setRecurrenceInterval(entity.getRecurrenceInterval());
        dto.setIsSent(entity.getIsSent());
        return dto;
    }
}
