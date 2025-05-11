package com.vuog.telebotmanager.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotStatistic implements Serializable {

    private Long totalBots;
    private Long activeBots;
    private Long totalCommands;
    private Long activeCommands;
    private Long totalScheduledMessages;
    private Long activeScheduledMessages;
}
