package com.vuog.telebotmanager.interfaces.dto.response;


import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for scheduled message responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMessageResponseDto {
    private Long id;
    private Long botId;
    private String botName;
    private String chatId;
    private String message;
    private LocalDateTime scheduledTime;
    private Boolean isSent;
    private LocalDateTime sentAt;
    private Boolean isCancelled;
    private String status;
    
    /**
     * Create a DTO from a ScheduledMessage entity
     */
    public static ScheduledMessageResponseDto fromEntity(ScheduledMessage scheduledMessage) {
        String status = "SCHEDULED";
        if (scheduledMessage.getIsSent()) {
            status = "SENT";
        } else if (scheduledMessage.getIsCancelled()) {
            status = "CANCELLED";
        }
        
        return ScheduledMessageResponseDto.builder()
                .id(scheduledMessage.getId())
                .botId(scheduledMessage.getBot().getId())
                .botName(scheduledMessage.getBot().getName())
                .chatId(scheduledMessage.getChatId())
                .message(scheduledMessage.getMessageText())
                .scheduledTime(scheduledMessage.getScheduledTime())
                .isSent(scheduledMessage.getIsSent())
                .sentAt(scheduledMessage.getSentAt())
                .isCancelled(scheduledMessage.getIsCancelled())
                .status(status)
                .build();
    }
}
