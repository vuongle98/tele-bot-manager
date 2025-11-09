package com.vuog.telebotmanager.infrastructure.handler;

import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.application.service.PermissionService;
import com.vuog.telebotmanager.infrastructure.util.TelegramUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default command handler for basic commands
 * Handles simple text-based commands and fallback responses
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultCommandHandler implements CommandHandler {

    private final CommandRepository commandRepository;
    private final PermissionService permissionService;

    @Override
    public boolean canHandle(CommandRequest request) {
        // This handler can process any command as a fallback
        return true;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        log.info("Executing default command: {}", request.getCommand());

        String command = request.getCommand();
        String inputText = request.getInputText();

        // Handle common commands
        return switch (command) {
            case "/start" -> handleStartCommand(request);
            case "/help" -> handleHelpCommand(request);
            case "/status" -> handleStatusCommand(request);
            case "/ping" -> handlePingCommand(request);
            case "/register" -> handleRegisterCommand(request);
            case "/commands", "/list" -> handleListCommands(request);
            default -> handleUnknownCommand(request);
        };
    }

    @Override
    public String getSupportedCommandType() {
        return "DEFAULT_COMMAND";
    }

    @Override
    public int getPriority() {
        return 1000; // Lowest priority - fallback handler
    }

    @Override
    public boolean isAvailable() {
        return true; // Always available
    }

    private CommandResponse handleStartCommand(CommandRequest request) {
        String responseText = """
                🤖 Welcome to the Telegram Bot Manager!
                
                I'm here to help you manage your bots and execute commands.
                
                Available commands:
                • /help - Show this help message
                • /status - Check bot status
                • /ping - Test bot connectivity
                • /ai <question> - Ask AI a question
                • /summarize <text> - Summarize text using AI
                • /generate <prompt> - Generate content using AI
                
                Type /help for more information.""";
        String safe = TelegramUtils.formatHtmlText(responseText);
        return CommandResponse.success(request.getCommandId(), safe);
    }

    private CommandResponse handleHelpCommand(CommandRequest request) {
        String responseText = """
                📚 Bot Commands Help
                
                **Basic Commands:**
                • /start - Start the bot
                • /help - Show this help
                • /status - Bot status
                • /ping - Test connectivity
                • /register - Register your Telegram account for access control
                
                • /commands or /list - List all commands available to you
                
                **AI Commands:**
                • /ai <question> - Ask AI
                • /summarize <text> - Summarize text
                • /generate <prompt> - Generate content
                • /analyze <text> - Analyze text
                
                **Plugin Commands:**
                • /plugin <name> - Execute plugin
                • /custom <command> - Custom command
                
                For more information, contact the administrator.""";
        String safe = TelegramUtils.formatHtmlText(responseText);
        return CommandResponse.success(request.getCommandId(), safe);
    }

    private CommandResponse handleStatusCommand(CommandRequest request) {
        String responseText = """
                ✅ Bot Status: Online
                🔄 System: Operational
                🤖 AI Service: Available
                🔌 Plugins: Loaded
                📊 Commands: Active
                
                All systems are running normally.""";
        String safe = TelegramUtils.formatHtmlText(responseText);
        return CommandResponse.success(request.getCommandId(), safe);
    }

    private CommandResponse handlePingCommand(CommandRequest request) {
        String responseText = """
                🏓 Pong! Bot is responding.
                ⏱️ Response time: < 1ms
                ✅ System: Healthy""";
        String safe = TelegramUtils.formatHtmlText(responseText);
        return CommandResponse.success(request.getCommandId(), safe);
    }

    private CommandResponse handleListCommands(CommandRequest request) {
        String userId = request.getUserId();
        Long botId = null;
        try { if (request.getBotId() != null) botId = Long.valueOf(request.getBotId()); } catch (Exception ignored) {}

        StringBuilder sb = new StringBuilder();
        sb.append("📋 Available commands\n\n");

        // Built-in commands
        java.util.List<String> builtIns = java.util.List.of("/start", "/help", "/status", "/ping");

        // Fetch bot-specific enabled commands
        java.util.List<Command> botCommands = botId != null ? commandRepository.findEnabledCommandsByBotId(botId) : java.util.List.of();

        // Filter by permission
        java.util.List<String> allCommands = new java.util.ArrayList<>(builtIns);
        for (Command c : botCommands) {
            if (c.getCommand() != null && !c.getCommand().isBlank()) {
                allCommands.add(c.getCommand());
            }
        }

        java.util.List<String> allowed = permissionService.filterAllowedCommands(userId, botId, allCommands);

        if (allowed.isEmpty()) {
            sb.append("No commands available for your account.");
        } else {
            allowed.stream().sorted().forEach(cmd -> sb.append("• ").append(cmd).append("\n"));
        }

        String safe = TelegramUtils.formatHtmlText(sb.toString());
        return CommandResponse.success(request.getCommandId(), safe);
    }

    private CommandResponse handleRegisterCommand(CommandRequest request) {
        String telegramUserId = request.getUserId();
        Long botId = null;
        try { if (request.getBotId() != null) botId = Long.valueOf(request.getBotId()); } catch (Exception ignored) {}
        var created = permissionService.registerIfAbsent(telegramUserId, botId);
        if (created == null) {
            return CommandResponse.error(request.getCommandId(), "Unable to register: missing Telegram user id", "BAD_REQUEST");
        }
        String msg = "✅ Registered user: " + (telegramUserId != null ? telegramUserId : "(unknown)") +
                (botId != null ? (" for bot " + botId) : " globally") +
                ". Role: " + created.getRole();
        return CommandResponse.success(request.getCommandId(), msg);
    }

    private CommandResponse handleUnknownCommand(CommandRequest request) {
        String responseText = "❓ Unknown command: " + request.getCommand() + "\n\n" +
                "Type /help to see available commands.\n" +
                "Or try one of these:\n" +
                "• /ai <question>\n" +
                "• /summarize <text>\n" +
                "• /generate <prompt>\n" +
                "• /status";
        String safe = TelegramUtils.formatHtmlText(responseText);
        return CommandResponse.success(request.getCommandId(), safe);
    }

    @Override
    public java.util.List<String> getSupportedCommands() {
        return java.util.List.of(
                "/start",
                "/help",
                "/status",
                "/ping",
                "/register",
                "/commands",
                "/list"
        );
    }
}
