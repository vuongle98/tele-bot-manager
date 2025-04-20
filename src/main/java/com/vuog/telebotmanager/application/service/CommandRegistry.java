package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.infrastructure.bot.CommandContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class CommandRegistry {

    private final Map<String, Consumer<CommandContext>> commandHandlers = new HashMap<>();

    public void registerCommand(String command, Consumer<CommandContext> handler) {
        commandHandlers.put(command, handler);
    }

    public Consumer<CommandContext> getHandler(String command) {
        return commandHandlers.get(command);
    }
}
