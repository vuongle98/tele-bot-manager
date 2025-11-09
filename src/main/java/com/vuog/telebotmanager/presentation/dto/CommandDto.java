package com.vuog.telebotmanager.presentation.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.vuog.telebotmanager.domain.entity.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Command entity responses
 * Contains only necessary fields for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandDto {

    private Long id;
    private Long botId;
    private String command;
    private String responseTemplate;
    private Boolean isEnabled;
    private String description;
    private Command.CommandType type;
    private Command.TriggerType trigger;
    private JsonNode parameters;
    private String pluginName;
    private String additionalConfig;
    private Integer priority;
    private Integer timeoutSeconds;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Create CommandDto from Command entity
     */
    public static CommandDto fromEntity(Command command) {
        return CommandDto.builder()
                .id(command.getId())
                .botId(command.getBot() != null ? command.getBot().getId() : null)
                .command(command.getCommand())
                .responseTemplate(command.getResponseTemplate())
                .isEnabled(command.getIsEnabled())
                .description(command.getDescription())
                .type(command.getType())
                .trigger(command.getTrigger())
                .parameters(command.getParameters())
                .pluginName(command.getPluginName())
                .additionalConfig(command.getAdditionalConfig())
                .priority(command.getPriority())
                .timeoutSeconds(command.getTimeoutSeconds())
                .retryCount(command.getRetryCount())
                .createdAt(command.getCreatedAt())
                .updatedAt(command.getUpdatedAt())
                .createdBy(command.getCreatedBy())
                .updatedBy(command.getUpdatedBy())
                .build();
    }
}
