package com.vuog.telebotmanager.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessMessageRequest {
    private String commandId;
    private String userId;
    private String chatId;
    private String command;
    private String inputText;
}
