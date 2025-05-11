package com.vuog.telebotmanager.interfaces.dto.query;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotQuery implements Serializable {

    private String name;
    private CommonEnum.BotStatus status;
    private Long ownerId;
    private Instant createdAfter;
    private Instant createdBefore;
    private CommonEnum.UpdateMethod updateMethod;
    private Boolean scheduled;
    private String search;
}
