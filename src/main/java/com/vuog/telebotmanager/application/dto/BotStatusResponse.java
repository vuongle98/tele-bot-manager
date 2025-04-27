// BotStatusResponse.java
package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.common.enums.CommonEnum;

public record BotStatusResponse(
    Long id,
    String name,
    CommonEnum.BotStatus status,
    CommonEnum.UpdateMethod updateMethod,
    String ownerUsername
) {}