package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.usecases.CommandHandlerUseCase;
import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import com.vuog.telebotmanager.domain.bot.model.BotLog;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.BotCommandRepository;
import com.vuog.telebotmanager.domain.bot.repository.BotLogRepository;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandlerServiceImpl implements CommandHandlerUseCase {

    private final BotLogRepository logRepository;
    private final TelegramBotRepository botRepository;
    private final BotCommandRepository commandRepository;
    private final BotRunner botRunner;
    private final MessageTemplateServiceImpl templateService;
    private final CommandDispatcher commandDispatcher;

    public void handleUpdate(Update update, Long botId) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();
                if (text.startsWith("/")) {
                    handleCommand(update, botId);
                } else {
                    // Log non-command messages for future use
                    logMessage(botId, update.getMessage().getChatId().toString(), text);
                }
            }
        } catch (Exception e) {
            log.error("Error handling update for bot {}: {}", botId, e.getMessage(), e);
        }
    }

    @Override
    public void handleCommand(Update update, Long botId) {
        String commandText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        log.info("Bot {} received command: {} from chat {}", botId, commandText, chatId);
        
        // Log the command
        logMessage(botId, chatId, commandText);
        
        try {
            // Parse the command (handle arguments if any)
            String[] parts = commandText.split("\\s+", 2);
            String baseCommand = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";

            baseCommand = baseCommand.startsWith("/") ? baseCommand.substring(1) : baseCommand;
            
            // Fetch bot-specific commands from database
            Optional<BotCommand> customCommand = commandRepository.findAllByIsEnabledTrueAndBotIdAndCommand(botId, baseCommand);
            
            if (customCommand.isPresent()) {
                // Handle custom command from database
                handleCustomCommand(update, botId, customCommand.get(), args);
            } else {
                // Handle built-in commands
                switch (baseCommand) {
                    case "start":
                        handleStartCommand(update, botId);
                        break;
                    case "help":
                        handleHelpCommand(update, botId);
                        break;
                    case "info":
                        handleInfoCommand(update, botId);
                        break;
                    case "settings":
                        handleSettingsCommand(update, botId, args);
                        break;
                    default:
                        handleUnknownCommand(update, botId);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error processing command '{}' for bot {}: {}", commandText, botId, e.getMessage(), e);
            sendErrorMessage(botId, chatId, "Sorry, an error occurred while processing your command.");
        }
    }
    
    /**
     * Handles custom commands defined in the database
     */
    private void handleCustomCommand(Update update, Long botId, BotCommand command, String args) {
        log.info("Handling custom command {} for bot {}", command.getCommand(), botId);
        String chatId = update.getMessage().getChatId().toString();
        
        try {
            // Get user information for template variables
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();
            
            // Create template variables
            Map<String, Object> templateVars = new java.util.HashMap<>();
            templateVars.put("username", username != null ? username : "User");
            templateVars.put("firstName", firstName != null ? firstName : "User");
            templateVars.put("lastName", lastName != null ? lastName : "");
            templateVars.put("botName", botRepository.findById(botId).map(TelegramBot::getName).orElse("Bot"));
            templateVars.put("args", args);
            
            // Render template from database
            String responseText = "Unknown response. Please check the command syntax and try again.";
            if (command.getHandlerMethod() != null) {
                responseText = commandDispatcher.dispatch(command.getHandlerMethod(), new String[]{args});
            } else if (command.getResponseTemplate() != null && !command.getResponseTemplate().isEmpty()) {
                responseText = templateService.renderTemplate(command.getResponseTemplate(), templateVars);
            }
            
            // Send response
            SendMessage message = createReplyMessage(chatId, responseText);
            message.enableMarkdown(true);
            sendMessage(botId, message);
            
        } catch (Exception e) {
            log.error("Error processing custom command {}: {}", command.getCommand(), e.getMessage(), e);
            sendErrorMessage(botId, chatId, "Error processing command: " + e.getMessage());
        }
    }
    
    private void handleStartCommand(Update update, Long botId) {
        log.info("Handling /start command for bot {}", botId);
        String chatId = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();
        String firstName = update.getMessage().getFrom().getFirstName();
        
        String welcomeMessage = templateService.renderTemplate(
                "👋 Welcome, {{name}}! I'm your Telegram bot assistant. \n\n" +
                "Use /help to see available commands.",
                java.util.Map.of("name", firstName != null ? firstName : (username != null ? username : "there"))
        );
        
        SendMessage message = createReplyMessage(chatId, welcomeMessage);
        // Add keyboard with common commands
        message.setReplyMarkup(createMainMenuKeyboard());
        
        sendMessage(botId, message);
    }
    
    private void handleHelpCommand(Update update, Long botId) {
        log.info("Handling /help command for bot {}", botId);
        String chatId = update.getMessage().getChatId().toString();
        
        StringBuilder helpTextBuilder = new StringBuilder("🤖 *Available Commands*:\n\n");
        
        // Add built-in commands
        helpTextBuilder.append("• /start - Start or restart the bot\n");
        helpTextBuilder.append("• /help - Show this help message\n");
        helpTextBuilder.append("• /info - Display information about this bot\n");
        helpTextBuilder.append("• /settings - Configure bot settings\n\n");
        
        // Add custom commands from database
        List<BotCommand> customCommands = commandRepository.findAllByIsEnabledTrueAndBotId(botId);
        if (!customCommands.isEmpty()) {
            helpTextBuilder.append("*Custom Commands*:\n\n");
            for (BotCommand cmd : customCommands) {
                String description = cmd.getDescription() != null && !cmd.getDescription().isEmpty() 
                    ? cmd.getDescription() 
                    : "Custom command";
                helpTextBuilder.append("• ").append(cmd.getCommand()).append(" - ").append(description).append("\n");
            }
            helpTextBuilder.append("\n");
        }
        
        helpTextBuilder.append("Need more help? Contact the bot administrator.");
        
        SendMessage message = createReplyMessage(chatId, helpTextBuilder.toString());
        message.enableMarkdown(true);
        
        sendMessage(botId, message);
    }
    
    private void handleInfoCommand(Update update, Long botId) {
        log.info("Handling /info command for bot {}", botId);
        String chatId = update.getMessage().getChatId().toString();
        
        Optional<TelegramBot> botOptional = botRepository.findById(botId);
        if (botOptional.isPresent()) {
            TelegramBot bot = botOptional.get();
            
            // Count custom commands
            long customCommandsCount = commandRepository.findAllByIsEnabledTrueAndBotId(botId).size();
            
            // Count bot logs for statistics
            long messageCount = logRepository.countByBotId(botId);

            String ownerId = bot.getOwner() != null ? bot.getOwner().getId().toString() : "No information available.";
            
            String infoText = "📊 *Bot Information*\n\n" +
                    "• Name: " + bot.getName() + "\n" +
                    "• Status: " + bot.getStatus() + "\n" +
                    "• Mode: " + (bot.getConfiguration().isWebhookEnabled() ? "Webhook" : "Long Polling") + "\n" +
                    "• Owner ID: " + ownerId + "\n" +
                    "• Custom Commands: " + customCommandsCount + "\n" +
                    "• Messages Processed: " + messageCount + "\n" +
                    (bot.getScheduled() != null && bot.getScheduled() ? "• Scheduling: Enabled\n" : "• Scheduling: Disabled\n");
            
            SendMessage message = createReplyMessage(chatId, infoText);
            message.enableMarkdown(true);
            
            sendMessage(botId, message);
        } else {
            sendErrorMessage(botId, chatId, "Bot information not available.");
        }
    }
    
    private void handleSettingsCommand(Update update, Long botId, String args) {
        log.info("Handling /settings command for bot {} with args: {}", botId, args);
        String chatId = update.getMessage().getChatId().toString();
        
        // This could be expanded with subcommands for different settings
        String settingsText = "⚙️ *Settings*\n\n" +
                "This is a placeholder for bot settings. In a real implementation, " +
                "you would be able to configure various bot options here.";
        
        SendMessage message = createReplyMessage(chatId, settingsText);
        message.enableMarkdown(true);
        
        sendMessage(botId, message);
    }
    
    private void handleUnknownCommand(Update update, Long botId) {
        log.info("Handling unknown command for bot {}", botId);
        String chatId = update.getMessage().getChatId().toString();
        String command = update.getMessage().getText();
        
        String errorMessage = "Sorry, I don't understand the command '" + command + "'.\n" +
                "Use /help to see available commands.";
        
        sendErrorMessage(botId, chatId, errorMessage);
    }
    
    private void sendErrorMessage(Long botId, String chatId, String errorText) {
        SendMessage message = createReplyMessage(chatId, "❌ " + errorText);
        sendMessage(botId, message);
    }
    
    private SendMessage createReplyMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }
    
    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/start"));
        row1.add(new KeyboardButton("/help"));
        
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/info"));
        row2.add(new KeyboardButton("/settings"));
        
        keyboard.add(row1);
        keyboard.add(row2);
        
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
    
    private void sendMessage(Long botId, SendMessage message) {
        try {
            BotHandler handler = botRunner.getHandler(botId);
            if (handler != null) {
                if (handler instanceof WebhookBotBase webhookBot) {
                    webhookBot.execute(message);
                } else if (handler instanceof LongPollingBotBase pollingBot) {
                    pollingBot.execute(message);
                }
                log.debug("Message sent to chat {} from bot {}", message.getChatId(), botId);
            } else {
                log.error("Bot handler not found for bot ID: {}", botId);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chat {} using bot {}: {}", 
                    message.getChatId(), botId, e.getMessage(), e);
        }
    }
    
    private void logMessage(Long botId, String chatId, String text) {
        try {
            BotLog log = new BotLog();
            log.setBotId(botId);
            log.setChatId(chatId);
            log.setMessage(text);
            log.setTimestamp(LocalDateTime.now());
            logRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to log message for bot {}: {}", botId, e.getMessage(), e);
        }
    }
}
