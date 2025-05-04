package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.infrastructure.bot.handler.CommandHandler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
public class CommandDispatcher {

    private final CommandHandler handlerService;

    public CommandDispatcher(CommandHandler handlerService) {
        this.handlerService = handlerService;
    }

    public String dispatch(String command, String[] args) {
        try {
            Method method = CommandHandler.class.getMethod(command, String[].class);
            return (String) method.invoke(handlerService, (Object) args);
        } catch (NoSuchMethodException e) {
            return "Unknown command.";
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}
