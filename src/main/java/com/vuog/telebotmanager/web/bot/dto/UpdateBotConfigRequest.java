package com.vuog.telebotmanager.web.bot.dto;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdateBotConfigRequest(
    @NotNull @Valid
    TelegramBot.BotConfiguration configuration,
    
    boolean restartOnUpdate
) {}