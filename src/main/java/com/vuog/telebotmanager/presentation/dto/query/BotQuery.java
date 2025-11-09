package com.vuog.telebotmanager.presentation.dto.query;

import com.vuog.telebotmanager.domain.entity.Bot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotQuery {
    private String username;
    private String name;
    private Bot.BotStatus status;
    private Boolean active;
}
