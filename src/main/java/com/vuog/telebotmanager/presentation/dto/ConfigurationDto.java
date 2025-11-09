package com.vuog.telebotmanager.presentation.dto;

import com.vuog.telebotmanager.domain.entity.Configuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Configuration entity responses
 * Contains only necessary fields for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationDto {

    private String id;
    private String keyName;
    private String value;
    private Configuration.ConfigurationType type;
    private Configuration.ConfigurationScope scope;
    private String description;
    private Boolean isActive;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Create ConfigurationDto from Configuration entity
     */
    public static ConfigurationDto fromEntity(Configuration configuration) {
        return ConfigurationDto.builder()
                .id(configuration.getId())
                .keyName(configuration.getKeyName())
                .value(configuration.getValue())
                .type(configuration.getType())
                .scope(configuration.getScope())
                .description(configuration.getDescription())
                .isActive(configuration.getIsActive())
                .version(configuration.getVersion())
                .createdAt(configuration.getCreatedAt())
                .updatedAt(configuration.getUpdatedAt())
                .createdBy(configuration.getCreatedBy())
                .updatedBy(configuration.getUpdatedBy())
                .build();
    }
}
