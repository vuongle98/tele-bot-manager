package com.vuog.telebotmanager.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ExecuteCommandRequest implements Serializable {

    private String commandId;
    private String botId;
    private String userId;
    private String chatId;
    private String command;
    private String inputText;
}
