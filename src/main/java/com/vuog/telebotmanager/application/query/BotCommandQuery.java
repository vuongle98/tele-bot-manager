package com.vuog.telebotmanager.application.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotCommandQuery implements Serializable {

    private String command;
    private String description;
}
