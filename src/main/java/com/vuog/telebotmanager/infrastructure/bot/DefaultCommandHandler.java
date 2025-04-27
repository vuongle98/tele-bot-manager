package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default handler for processing bot commands
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCommandHandler {

    /**
     * Handle a command with the provided context
     * 
     * @param command The bot command to handle
     * @param context The context of the command execution
     */
    public void handleCommand(BotCommand command, CommandContext context) {
        log.debug("Handling command: {} with args: {}", command.getCommand(), context.getArgs());
        
        // Get the response template from the command
        String responseTemplate = command.getResponseTemplate();
        
        // Process the template with context variables
        String processedResponse = processTemplate(responseTemplate, context);
        
        // Send the response
        context.sendResponse(processedResponse);
        
        log.debug("Command handled successfully: {}", command.getCommand());
    }
    
    /**
     * Process a template string by replacing placeholders with values from the context
     * 
     * @param template The template string
     * @param context The command context
     * @return The processed template
     */
    private String processTemplate(String template, CommandContext context) {
        String result = template;
        
        // Replace common placeholders
        result = result.replace("{{firstName}}", context.getFirstName() != null ? context.getFirstName() : "User");
        result = result.replace("{{lastName}}", context.getLastName() != null ? context.getLastName() : "");
        result = result.replace("{{username}}", context.getUsername() != null ? context.getUsername() : "user");
        result = result.replace("{{botName}}", context.getBotName() != null ? context.getBotName() : "Bot");
        result = result.replace("{{args}}", context.getArgs() != null ? context.getArgs() : "");
        
        return result;
    }
}
