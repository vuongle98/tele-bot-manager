package com.vuog.telebotmanager.interfaces.dto.request;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating a bot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBotRequest {
    private String name;
    private String apiToken;
    private Boolean scheduled;
    private ConfigurationRequest configuration;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigurationRequest {
        private CommonEnum.UpdateMethod updateMethod;
        private String webhookUrl;
        private Integer maxConnections;
        private String allowedUpdates;
        private Boolean isWebhookEnabled;
        private Integer pollingTimeout;
        private Integer pollingLimit;
    }
}
