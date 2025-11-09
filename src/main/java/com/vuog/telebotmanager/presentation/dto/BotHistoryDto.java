package com.vuog.telebotmanager.presentation.dto;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for BotHistory entity responses
 * Contains only necessary fields for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotHistoryDto {

    private Long id;
    private Long botId;
    private Bot.BotStatus previousStatus;
    private Bot.BotStatus newStatus;
    private LocalDateTime timestamp;
    private String notes;
    private String errorDetails;
    private String triggeredBy;

    /**
     * Create BotHistoryDto from BotHistory entity
     */
    public static BotHistoryDto fromEntity(BotHistory history) {
        return BotHistoryDto.builder()
                .id(history.getId())
                .botId(history.getBot() != null ? history.getBot().getId() : null)
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .timestamp(history.getTimestamp())
                .notes(history.getNotes())
                .errorDetails(history.getErrorDetails())
                .triggeredBy(history.getTriggeredBy())
                .build();
    }
}
