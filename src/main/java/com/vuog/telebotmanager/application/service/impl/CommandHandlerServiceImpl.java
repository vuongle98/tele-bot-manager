package com.vuog.telebotmanager.application.service.impl;


import com.vuog.telebotmanager.application.service.BotHandlerRegistry;
import com.vuog.telebotmanager.application.service.CommandRegistry;
import com.vuog.telebotmanager.infrastructure.bot.BotHandler;
import com.vuog.telebotmanager.infrastructure.bot.CommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandlerServiceImpl {

    private final BotHandlerRegistry botHandlerRegistry;
    private final CommandRegistry commandRegistry;

    public void handleUpdate(Update update, Long botId) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            String text = msg.getText();

            if (text.startsWith("/")) {
                String[] parts = text.split("\\s+", 2);
                String command = parts[0].substring(1);
                String args = parts.length > 1 ? parts[1] : "";

                Consumer<CommandContext> handler = commandRegistry.getHandler(command);
                if (handler != null) {
                    try {
                        BotHandler botHandler = botHandlerRegistry.getHandler(botId);
                        CommandContext context = new CommandContext(
                                msg.getChatId().toString(),
                                msg.getFrom().getId(),
                                args,
                                msg,
                                botHandler
                        );
                        handler.accept(context);
                    } catch (Exception e) {
                        log.error("Error while handling command: /{}", command, e);
                    }
                }
            }
        }
    }
}
