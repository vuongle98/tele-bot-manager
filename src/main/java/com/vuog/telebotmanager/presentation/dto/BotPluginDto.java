package com.vuog.telebotmanager.presentation.dto;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for BotPlugin entity responses
 * Contains only necessary fields for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotPluginDto {

    private String id;
    private String name;
    private String description;
    private BotPlugin.PluginType type;
    private BotPlugin.PluginStatus status;
    private String version;
    private String className;
    private String methodName;
    private Boolean isActive;
    private String author;
    private Long botId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Create BotPluginDto from BotPlugin entity
     */
    public static BotPluginDto fromEntity(BotPlugin botPlugin) {
        return BotPluginDto.builder()
                .id(botPlugin.getId())
                .name(botPlugin.getName())
                .description(botPlugin.getDescription())
                .type(botPlugin.getType())
                .status(botPlugin.getStatus())
                .version(botPlugin.getVersion())
                .className(botPlugin.getClassName())
                .methodName(botPlugin.getMethodName())
                .isActive(botPlugin.getIsActive())
                .author(botPlugin.getAuthor())
                .botId(botPlugin.getBot() != null ? botPlugin.getBot().getId() : null)
                .createdAt(botPlugin.getCreatedAt())
                .updatedAt(botPlugin.getUpdatedAt())
                .createdBy(botPlugin.getCreatedBy())
                .updatedBy(botPlugin.getUpdatedBy())
                .build();
    }
}
