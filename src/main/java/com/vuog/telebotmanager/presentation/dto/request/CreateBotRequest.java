package com.vuog.telebotmanager.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBotRequest {
    private String botToken;
    private String botUsername;
    private String botName;
    private String webhookUrl;
    private Boolean isActive;
    private String description;
}
