package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * DTO for scheduled message responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMessageResponseDto {
    
    private Long id;
    private Long botId;
    private String chatId;
    private String messageText;
    private LocalDateTime scheduledTime;
    private Boolean isRecurring;
    private String recurrencePattern;
    private Boolean isSent;
    private LocalDateTime sentAt;
    private Boolean isCancelled;
    
    /**
     * Convert entity to DTO
     */
    public static ScheduledMessageResponseDto fromEntity(ScheduledMessage entity) {
        ScheduledMessageResponseDto dto = new ScheduledMessageResponseDto();
        dto.setId(entity.getId());
        dto.setBotId(entity.getBot().getId());
        dto.setChatId(entity.getChatId());
        dto.setMessageText(entity.getMessageText());
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setIsRecurring(entity.getIsRecurring() != null && entity.getIsRecurring());
        
        // Format recurrence interval for display
        if (entity.getIsRecurring() != null && entity.getIsRecurring() && entity.getRecurrenceInterval() != null) {
            dto.setRecurrencePattern(formatRecurrenceInterval(entity.getRecurrenceInterval()));
        }
        
        dto.setIsSent(entity.getIsSent());
        dto.setSentAt(entity.getSentAt());
        dto.setIsCancelled(entity.getIsCancelled());
        
        return dto;
    }
    
    /**
     * Format recurrence interval into a human-readable format
     */
    private static String formatRecurrenceInterval(Duration interval) {
        long days = interval.toDays();
        long hours = interval.toHoursPart();
        long minutes = interval.toMinutesPart();
        
        StringBuilder pattern = new StringBuilder();
        
        if (days > 0) {
            pattern.append(days).append(days == 1 ? " day" : " days");
            if (hours > 0 || minutes > 0) pattern.append(", ");
        }
        
        if (hours > 0) {
            pattern.append(hours).append(hours == 1 ? " hour" : " hours");
            if (minutes > 0) pattern.append(", ");
        }
        
        if (minutes > 0 || (days == 0 && hours == 0)) {
            pattern.append(minutes).append(minutes == 1 ? " minute" : " minutes");
        }
        
        return "Every " + pattern.toString();
    }
}