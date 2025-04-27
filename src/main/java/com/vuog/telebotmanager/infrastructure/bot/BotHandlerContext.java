package com.vuog.telebotmanager.infrastructure.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BotHandlerContext {
    private BotHandler handler;
    private Message message;
}
