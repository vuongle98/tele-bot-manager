package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;

public record BotConfigDto(
        CommonEnum.UpdateMethod updateMethod,
        String webhookUrl,
        Integer maxConnections,
        String allowedUpdates,
        Boolean isWebhookEnabled
//        String ipAddress,
//        Boolean dropPendingUpdates,
//        String secretToken,
//        Integer maxThreads
) {
    public BotConfigDto {
        if (updateMethod == null) updateMethod = CommonEnum.UpdateMethod.LONG_POLLING;
        if (maxConnections == null) maxConnections = 40;
        if (allowedUpdates == null) allowedUpdates = "message,callback_query";
//        if (dropPendingUpdates == null) dropPendingUpdates = false;
    }

    public static BotConfigDto fromEntity(TelegramBot.BotConfiguration config) {
        return new BotConfigDto(
                config.getUpdateMethod(),
                config.getWebhookUrl(),
                config.getMaxConnections(),
                config.getAllowedUpdates(),
                config.isWebhookEnabled()
//                config.getIpAddress(),
//                config.getDropPendingUpdates(),
//                config.getSecretToken(),
//                config.getMaxThreads()
        );
    }
}