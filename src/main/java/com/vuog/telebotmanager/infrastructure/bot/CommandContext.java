//package com.vuog.telebotmanager.infrastructure.bot;
//
//import lombok.extern.slf4j.Slf4j;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//
//@Slf4j
//public record CommandContext(String chatId, Long userId, String args, Message message, BotHandler botHandler) {
//    public void reply(String text) {
//        try {
//            botHandler.getSender().execute(new SendMessage(chatId, text));
//        } catch (Exception e) {
//            log.error("Failed to send reply to chat {} using bot {}: {}", chatId, botHandler.getBotId(), e.getMessage());
//        }
//    }
//}
package com.vuog.telebotmanager.infrastructure.bot;

import lombok.Builder;
import lombok.Data;
import java.util.function.Consumer;

/**
 * Context for command execution, containing all necessary information
 * about the command, user, and methods to respond
 */
@Data
@Builder
public class CommandContext {
    
    // User information
    private String firstName;
    private String lastName;
    private String username;
    private Long userId;
    
    // Bot information
    private String botName;
    private Long botId;
    
    // Command information
    private String command;
    private String args;
    private Long chatId;
    
    // Response handler
    private Consumer<String> responseHandler;
    
    /**
     * Send a response back to the user
     * 
     * @param message The message to send
     */
    public void sendResponse(String message) {
        if (responseHandler != null) {
            responseHandler.accept(message);
        }
    }
}