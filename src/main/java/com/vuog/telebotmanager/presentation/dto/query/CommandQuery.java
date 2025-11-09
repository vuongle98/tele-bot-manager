package com.vuog.telebotmanager.presentation.dto.query;

import com.vuog.telebotmanager.domain.entity.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandQuery {
    private Long botId;
    private Command.CommandType type;
    private Command.TriggerType trigger;
    private Boolean enabled;
    private String commandContains;
}
