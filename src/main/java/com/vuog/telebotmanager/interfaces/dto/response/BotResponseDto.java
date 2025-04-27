package com.vuog.telebotmanager.interfaces.dto.response;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for bot list responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotResponseDto {
    private Long id;
    private String name;
    private CommonEnum.BotStatus status;
    private String updateMethod;
    private Boolean isScheduled;
    
    /**
     * Create a DTO from a TelegramBot entity
     */
    public static BotResponseDto fromEntity(TelegramBot bot) {
        return BotResponseDto.builder()
                .id(bot.getId())
                .name(bot.getName())
                .status(bot.getStatus())
                .updateMethod(bot.getConfiguration() != null ? 
                        bot.getConfiguration().getUpdateMethod().name() : "UNKNOWN")
                .isScheduled(bot.getScheduled() != null && bot.getScheduled())
                .build();
    }
}
