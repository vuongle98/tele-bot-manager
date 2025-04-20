// CreateBotRequest.java
package com.vuog.telebotmanager.web.bot.dto;

import com.vuog.telebotmanager.domain.user.model.User;

public record CreateBotRequest(
    String name,
    String apiToken,
    BotConfigDto configuration,
    User owner
) {}