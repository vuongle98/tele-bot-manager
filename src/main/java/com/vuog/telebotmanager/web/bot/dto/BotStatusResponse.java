// BotStatusResponse.java
package com.vuog.telebotmanager.web.bot.dto;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot.BotStatus;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot.UpdateMethod;

public record BotStatusResponse(
    Long id,
    String name,
    BotStatus status,
    UpdateMethod updateMethod,
    String ownerUsername
) {}