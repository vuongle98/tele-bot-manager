package com.vuog.telebotmanager.presentation.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.vuog.telebotmanager.domain.entity.Command;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateCommandRequest implements Serializable {
    private Long botId;
    private String command;
    private String description;
    private Command.CommandType type;
    private Command.TriggerType trigger;
    private JsonNode parameters;
    private Command.Category category;
    private String responseTemplate;
    private String pluginName;
    private String createdBy;
    private Integer priority;
    private Integer retryCount;
    private Integer timeoutSeconds;
}