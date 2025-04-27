package com.vuog.telebotmanager.interfaces.dto.response;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.BotHistory;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for bot status history responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotHistoryResponseDto {
    private Long id;
    private Long botId;
    private String botName;
    private CommonEnum.BotStatus previousStatus;
    private CommonEnum.BotStatus newStatus;
    private LocalDateTime timestamp;
    private String notes;
    private String errorDetails;
    
    /**
     * Create a DTO from a BotHistory entity
     */
    public static BotHistoryResponseDto fromEntity(BotHistory history) {
        return BotHistoryResponseDto.builder()
                .id(history.getId())
                .botId(history.getBot().getId())
                .botName(history.getBot().getName())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .timestamp(history.getTimestamp())
                .notes(history.getNotes())
                .errorDetails(history.getErrorDetails())
                .build();
    }
}
