package com.vuog.telebotmanager.presentation.dto.query;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PluginQuery {
    private String nameContains;
    private BotPlugin.PluginType type;
    private BotPlugin.PluginStatus status;
    private Boolean active;
    private String author;
}
