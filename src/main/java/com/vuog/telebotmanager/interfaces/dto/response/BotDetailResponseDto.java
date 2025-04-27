package com.vuog.telebotmanager.interfaces.dto.response;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for detailed bot information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotDetailResponseDto {
    private Long id;
    private String name;
    private CommonEnum.BotStatus status;
    private String apiToken;
    private Boolean isScheduled;
    private ConfigurationDto configuration;
    private String ownerUsername;
    private Long ownerId;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigurationDto {
        private Long id;
        private String updateMethod;
        private String webhookUrl;
        private Integer maxConnections;
        private String allowedUpdates;
        private Boolean isWebhookEnabled;
        private Integer pollingTimeout;
        private Integer pollingLimit;
    }
    
    /**
     * Create a detailed DTO from a TelegramBot entity
     */
    public static BotDetailResponseDto fromEntity(TelegramBot bot) {
        ConfigurationDto configDto = null;
        if (bot.getConfiguration() != null) {
            TelegramBot.BotConfiguration config = bot.getConfiguration();
            configDto = ConfigurationDto.builder()
                    .id(config.getId())
                    .updateMethod(config.getUpdateMethod().name())
                    .webhookUrl(config.getWebhookUrl())
                    .maxConnections(config.getMaxConnections())
                    .allowedUpdates(config.getAllowedUpdates())
                    .isWebhookEnabled(config.isWebhookEnabled())
                    .pollingTimeout(config.getPollingTimeout())
                    .pollingLimit(config.getPollingLimit())
                    .build();
        }
        
        return BotDetailResponseDto.builder()
                .id(bot.getId())
                .name(bot.getName())
                .status(bot.getStatus())
                .apiToken(bot.getApiToken())
                .isScheduled(bot.getScheduled() != null && bot.getScheduled())
                .configuration(configDto)
                .ownerUsername(bot.getOwner() != null ? bot.getOwner().getUsername() : null)
                .ownerId(bot.getOwner() != null ? bot.getOwner().getId() : null)
                .build();
    }
}
