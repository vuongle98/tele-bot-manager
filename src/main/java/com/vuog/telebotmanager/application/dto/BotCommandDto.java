package com.vuog.telebotmanager.application.dto;

import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotCommandDto implements Serializable {

    private Long id;
    private String command;
    private String description;
    private String responseTemplate;
    private String additionalConfig;
    private String handlerMethod;

    public BotCommandDto(BotCommand command) {
        this.id = command.getId();
        this.command = command.getCommand();
        this.description = command.getDescription();
        this.responseTemplate = command.getResponseTemplate();
        this.additionalConfig = command.getAdditionalConfig();
        this.handlerMethod = command.getHandlerMethod();
    }
}
