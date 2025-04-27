package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;

public record BotResponseDto(
        Long id,
        String name,
        CommonEnum.BotStatus status,
        BotConfigDto configuration,
        Long ownerId,
        String apiToken,
        Boolean scheduled
) {
    public static BotResponseDto fromEntity(TelegramBot bot) {
        Long ownerId = bot.getOwner() == null ? null : bot.getOwner().getId();
        return new BotResponseDto(
                bot.getId(),
                bot.getName(),
                bot.getStatus(),
                BotConfigDto.fromEntity(bot.getConfiguration()),
                ownerId,
                bot.getApiToken(),
                bot.getScheduled()
        );
    }
}