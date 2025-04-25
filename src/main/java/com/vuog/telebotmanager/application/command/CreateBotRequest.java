// CreateBotRequest.java
package com.vuog.telebotmanager.application.command;

import com.vuog.telebotmanager.application.dto.BotConfigDto;
import com.vuog.telebotmanager.domain.user.model.User;

public record CreateBotRequest(
    String name,
    String apiToken,
    BotConfigDto configuration,
    User owner
) {}