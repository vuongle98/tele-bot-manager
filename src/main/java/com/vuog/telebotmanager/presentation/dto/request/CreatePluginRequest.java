package com.vuog.telebotmanager.presentation.dto.request;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreatePluginRequest implements Serializable {
    private String name;
    private String description;
    private String sourceCode;
    private BotPlugin.PluginType type;
    private String className;
    private String methodName;
    private String author;
    private String createdBy;
    private Long botId;
}
