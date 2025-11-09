package com.vuog.telebotmanager.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBotRequest {
    private String botName;
    private String description;
    private String webhookUrl;
}
