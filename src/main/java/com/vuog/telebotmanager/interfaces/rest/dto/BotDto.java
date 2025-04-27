package com.vuog.telebotmanager.interfaces.rest.dto;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.Data;

@Data
public class BotDto {
    private Long id;
    private String name;
    private boolean active;

    public static BotDto fromEntity(TelegramBot entity) {
        BotDto dto = new BotDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        // Active status could be determined based on whether the bot has a registered handler
        // For now, we'll just set it to true if the bot exists
        dto.setActive(entity != null);
        return dto;
    }
}
