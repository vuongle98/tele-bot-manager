package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;

public record BotResponseDto(
        Long id,
        String name,
        TelegramBot.BotStatus status,
        BotConfigDto configuration,
        Long ownerId,
        String apiToken,
        Boolean scheduled
) {
    public static BotResponseDto fromEntity(TelegramBot bot) {
        return new BotResponseDto(
                bot.getId(),
                bot.getName(),
                bot.getStatus(),
                BotConfigDto.fromEntity(bot.getConfiguration()),
                bot.getOwner().getId(),
                bot.getApiToken(),
                bot.getScheduled()
        );
    }
}