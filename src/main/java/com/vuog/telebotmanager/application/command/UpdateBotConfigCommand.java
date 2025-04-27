package com.vuog.telebotmanager.application.command;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdateBotConfigCommand(
    @NotNull @Valid
    TelegramBot.BotConfiguration configuration,
    
    boolean restartOnUpdate
) {}