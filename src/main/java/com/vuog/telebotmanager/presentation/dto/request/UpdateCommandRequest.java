package com.vuog.telebotmanager.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdateCommandRequest implements Serializable {
    private String description;
    private String parameters;
    private String additionalConfig;
    private String responseTemplate;
    private String pluginName;
    private String updatedBy;
    private Integer priority;
}
