package com.vuog.telebotmanager.presentation.dto;

import com.vuog.telebotmanager.domain.entity.Bot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Bot entity responses
 * Contains only necessary fields for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotDto {

    private Long id;
    private String botUsername;
    private String botName;
    private Bot.BotStatus status;
    private String webhookUrl;
    private Boolean isActive;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Create BotDto from Bot entity
     */
    public static BotDto fromEntity(Bot bot) {
        return BotDto.builder()
                .id(bot.getId())
                .botUsername(bot.getBotUsername())
                .botName(bot.getBotName())
                .status(bot.getStatus())
                .webhookUrl(bot.getWebhookUrl())
                .isActive(bot.getIsActive())
                .description(bot.getDescription())
                .createdAt(bot.getCreatedAt())
                .updatedAt(bot.getUpdatedAt())
                .createdBy(bot.getCreatedBy())
                .updatedBy(bot.getUpdatedBy())
                .build();
    }
}
