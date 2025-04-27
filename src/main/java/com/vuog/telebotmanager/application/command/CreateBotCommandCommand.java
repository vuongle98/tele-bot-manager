package com.vuog.telebotmanager.application.command;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBotCommandCommand implements Serializable {

    private String command;
    private String responseTemplate;
    private String description;
    private String additionalConfig;
}
