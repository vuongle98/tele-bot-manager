package com.vuog.telebotmanager.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for Telegram bot operations
 * Provides helper methods for creating messages, keyboards, and other Telegram-specific functionality
 */
@Component
@Slf4j
public class TelegramUtils {

    /**
     * Create a simple text message
     */
    public static SendMessage createTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("HTML");
        return message;
    }

    /**
     * Create a text message with inline keyboard
     */
    public static SendMessage createMessageWithInlineKeyboard(String chatId, String text, List<List<InlineKeyboardButton>> buttons) {
        SendMessage message = createTextMessage(chatId, text);

        if (buttons != null && !buttons.isEmpty()) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.setKeyboard(buttons);
            message.setReplyMarkup(keyboard);
        }

        return message;
    }

    /**
     * Create a text message with reply keyboard
     */
    public static SendMessage createMessageWithReplyKeyboard(String chatId, String text, List<List<String>> buttonRows) {
        SendMessage message = createTextMessage(chatId, text);

        if (buttonRows != null && !buttonRows.isEmpty()) {
            ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
            keyboard.setResizeKeyboard(true);
            keyboard.setOneTimeKeyboard(false);
            keyboard.setSelective(false);

            List<KeyboardRow> rows = new ArrayList<>();
            for (List<String> row : buttonRows) {
                KeyboardRow keyboardRow = new KeyboardRow();
                for (String buttonText : row) {
                    keyboardRow.add(new KeyboardButton(buttonText));
                }
                rows.add(keyboardRow);
            }

            keyboard.setKeyboard(rows);
            message.setReplyMarkup(keyboard);
        }

        return message;
    }

    /**
     * Create an inline keyboard button
     */
    public static InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * Create an inline keyboard button with URL
     */
    public static InlineKeyboardButton createInlineButtonWithUrl(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return button;
    }

    /**
     * Create a row of inline keyboard buttons
     */
    public static List<InlineKeyboardButton> createInlineButtonRow(String... buttonTexts) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String text : buttonTexts) {
            row.add(createInlineButton(text, text));
        }
        return row;
    }

    /**
     * Create a row of inline keyboard buttons with custom callback data
     */
    public static List<InlineKeyboardButton> createInlineButtonRowWithData(String[][] buttonData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String[] data : buttonData) {
            if (data.length >= 2) {
                row.add(createInlineButton(data[0], data[1]));
            }
        }
        return row;
    }

    /**
     * Create a photo message
     */
//    public static SendPhoto createPhotoMessage(String chatId, String photoUrl, String caption) {
//        SendPhoto photo = new SendPhoto();
//        photo.setChatId(chatId);
//        photo.setPhoto(photoUrl);
//        if (caption != null && !caption.trim().isEmpty()) {
//            photo.setCaption(caption);
//        }
//        return photo;
//    }

    /**
     * Create a help menu keyboard
     */
    public static List<List<InlineKeyboardButton>> createHelpMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // First row
        keyboard.add(createInlineButtonRow("🤖 Bot Status", "📊 Statistics"));

        // Second row
        keyboard.add(createInlineButtonRow("❓ Help", "⚙️ Settings"));

        // Third row
        keyboard.add(createInlineButtonRow("🔧 Commands", "🔌 Plugins"));

        return keyboard;
    }

    /**
     * Create a command menu keyboard
     */
    public static List<List<InlineKeyboardButton>> createCommandMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // AI Commands
        keyboard.add(createInlineButtonRow("🤖 Ask AI", "📝 Summarize"));
        keyboard.add(createInlineButtonRow("✨ Generate", "🔍 Analyze"));

        // Utility Commands
        keyboard.add(createInlineButtonRow("🏓 Ping", "📊 Status"));

        return keyboard;
    }

    /**
     * Create a settings menu keyboard
     */
    public static List<List<InlineKeyboardButton>> createSettingsMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Settings options
        keyboard.add(createInlineButtonRow("🔔 Notifications", "🌐 Language"));
        keyboard.add(createInlineButtonRow("🎨 Theme", "⚡ Performance"));
        keyboard.add(createInlineButtonRow("🔒 Privacy", "📱 Interface"));

        return keyboard;
    }

    /**
     * Format text with HTML markup
     */
    public static String formatHtmlText(String text) {
        if (text == null) return "";

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Create a formatted help message
     */
    public static String createHelpMessage() {
        return """
                🤖 <b>Telegram Bot Manager Help</b>
                
                <b>Basic Commands:</b>
                • /start - Start the bot
                • /help - Show this help
                • /status - Bot status
                • /ping - Test connectivity
                
                <b>AI Commands:</b>
                • /ai <question> - Ask AI
                • /summarize <text> - Summarize text
                • /generate <prompt> - Generate content
                • /analyze <text> - Analyze text
                
                <b>Plugin Commands:</b>
                • /botPlugin <name> - Execute botPlugin
                • /custom <command> - Custom command
                
                <b>Admin Commands:</b>
                • /admin - Admin panel
                • /logs - View logs
                • /config - Configuration
                
                For more information, contact the administrator.
                """;
    }

    /**
     * Create a formatted status message
     */
    public static String createStatusMessage(String botUsername, boolean isActive, long commandCount) {
        return String.format("""
                🤖 <b>Bot Status</b>
                
                <b>Bot:</b> @%s
                <b>Status:</b> %s
                <b>Commands:</b> %d
                <b>Uptime:</b> Online
                <b>Version:</b> 1.0.0
                
                All systems operational! ✅
                """, botUsername, isActive ? "🟢 Active" : "🔴 Inactive", commandCount);
    }

    /**
     * Create a formatted error message
     */
    public static String createErrorMessage(String error, String details) {
        return String.format("""
                ❌ <b>Error</b>
                
                <b>Error:</b> %s
                <b>Details:</b> %s
                
                Please try again or contact support.
                """, error, details);
    }

    /**
     * Create a formatted success message
     */
    public static String createSuccessMessage(String message) {
        return String.format("""
                ✅ <b>Success</b>
                
                %s
                
                Operation completed successfully!
                """, message);
    }
}
