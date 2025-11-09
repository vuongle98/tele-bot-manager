package com.vuog.telebotmanager.infrastructure.command;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service for creating default commands for bots
 * Provides standard bot interaction commands
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultBotCommands {

    private final BotRepository botRepository;
    private final CommandRepository commandRepository;

    /**
     * Create default commands for a bot
     */
    public void createDefaultCommands(Long botId) {
        log.info("Creating default commands for bot: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        // Create start command
        createStartCommand(bot);

        // Create help command
        createHelpCommand(bot);

        // Create status command
        createStatusCommand(bot);

        // Create ping command
        createPingCommand(bot);

        // Create info command
        createInfoCommand(bot);

        // Create settings command
        createSettingsCommand(bot);

        log.info("Created default commands for bot: {}", botId);
    }

    private void createStartCommand(Bot bot) {
        Command startCommand = Command.builder()
                .bot(bot)
                .command("/start")
                .responseTemplate("🤖 Welcome to {bot_name}!\n\nI'm your personal assistant bot. Type /help to see what I can do for you.")
                .isEnabled(true)
                .description("Welcome message and bot introduction")
                .type(Command.CommandType.CUSTOM)
                .trigger(Command.TriggerType.MANUAL)
                .priority(1)
                .timeoutSeconds(5)
                .retryCount(0)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(startCommand);
    }

    private void createHelpCommand(Bot bot) {
        Command helpCommand = Command.builder()
                .bot(bot)
                .command("/help")
                .responseTemplate("📚 <b>Available Commands:</b>\n\n" +
                        "• /start - Start the bot\n" +
                        "• /help - Show this help\n" +
                        "• /status - Bot status\n" +
                        "• /ping - Test connectivity\n" +
                        "• /info - Bot information\n" +
                        "• /settings - Bot settings\n\n" +
                        "• /ai <question> - Ask AI\n" +
                        "• /summarize <text> - Summarize text\n" +
                        "• /generate <prompt> - Generate content\n" +
                        "• /analyze <text> - Analyze text\n\n" +
                        "For more information, contact the administrator.")
                .isEnabled(true)
                .description("Show available commands and help")
                .type(Command.CommandType.CUSTOM)
                .trigger(Command.TriggerType.MANUAL)
                .priority(2)
                .timeoutSeconds(5)
                .retryCount(0)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(helpCommand);
    }

    private void createStatusCommand(Bot bot) {
        Command statusCommand = Command.builder()
                .bot(bot)
                .command("/status")
                .responseTemplate("✅ <b>Bot Status</b>\n\n" +
                        "🤖 Bot: @{bot_username}\n" +
                        "🔄 Status: {bot_status}\n" +
                        "📊 Commands: {command_count}\n" +
                        "🔌 Plugins: {plugin_count}\n" +
                        "⏰ Uptime: Online\n\n" +
                        "All systems operational! ✅")
                .isEnabled(true)
                .description("Show bot status and system information")
                .type(Command.CommandType.CUSTOM)
                .trigger(Command.TriggerType.MANUAL)
                .priority(3)
                .timeoutSeconds(5)
                .retryCount(0)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(statusCommand);
    }

    private void createPingCommand(Bot bot) {
        Command pingCommand = Command.builder()
                .bot(bot)
                .command("/ping")
                .responseTemplate("🏓 Pong! Bot is responding.\n" +
                        "⏱️ Response time: < 1ms\n" +
                        "✅ System: Healthy")
                .isEnabled(true)
                .description("Test bot connectivity and response time")
                .type(Command.CommandType.CUSTOM)
                .trigger(Command.TriggerType.MANUAL)
                .priority(4)
                .timeoutSeconds(3)
                .retryCount(0)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(pingCommand);
    }

    private void createInfoCommand(Bot bot) {
        Command infoCommand = Command.builder()
                .bot(bot)
                .command("/info")
                .responseTemplate("ℹ️ <b>Bot Information</b>\n\n" +
                        "🤖 Name: {bot_name}\n" +
                        "👤 Username: @{bot_username}\n" +
                        "📅 Created: {created_at}\n" +
                        "🔄 Last Updated: {updated_at}\n" +
                        "👨‍💻 Created By: {created_by}\n\n" +
                        "This bot is powered by Telegram Bot Manager with AI capabilities.")
                .isEnabled(true)
                .description("Show detailed bot information")
                .type(Command.CommandType.CUSTOM)
                .trigger(Command.TriggerType.MANUAL)
                .priority(5)
                .timeoutSeconds(5)
                .retryCount(0)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(infoCommand);
    }

    private void createSettingsCommand(Bot bot) {
        Command settingsCommand = Command.builder()
                .bot(bot)
                .command("/settings")
                .responseTemplate("⚙️ <b>Bot Settings</b>\n\n" +
                        "🔔 Notifications: Enabled\n" +
                        "🌐 Language: English\n" +
                        "🎨 Theme: Default\n" +
                        "⚡ Performance: Optimized\n" +
                        "🔒 Privacy: Protected\n\n" +
                        "Use the buttons below to modify settings:")
                .isEnabled(true)
                .description("Show bot settings and configuration options")
                .type(Command.CommandType.CUSTOM)
                .trigger(Command.TriggerType.MANUAL)
                .priority(6)
                .timeoutSeconds(5)
                .retryCount(0)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(settingsCommand);
    }

    /**
     * Create AI-powered commands for a bot
     */
    public void createAiCommands(Long botId) {
        log.info("Creating AI commands for bot: {}", botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));

        // Create AI answer command
        createAiAnswerCommand(bot);

        // Create summarize command
        createSummarizeCommand(bot);

        // Create generate command
        createGenerateCommand(bot);

        // Create analyze command
        createAnalyzeCommand(bot);

        log.info("Created AI commands for bot: {}", botId);
    }

    private void createAiAnswerCommand(Bot bot) {
        Command aiCommand = Command.builder()
                .bot(bot)
                .command("/ai")
                .responseTemplate("🤖 AI Response: {ai_response}")
                .isEnabled(true)
                .description("Ask AI a question")
                .type(Command.CommandType.AI_ANSWER)
                .trigger(Command.TriggerType.MANUAL)
                .priority(10)
                .timeoutSeconds(30)
                .retryCount(1)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(aiCommand);
    }

    private void createSummarizeCommand(Bot bot) {
        Command summarizeCommand = Command.builder()
                .bot(bot)
                .command("/summarize")
                .responseTemplate("📝 Summary: {ai_summary}")
                .isEnabled(true)
                .description("Summarize text using AI")
                .type(Command.CommandType.SUMMARY)
                .trigger(Command.TriggerType.MANUAL)
                .priority(11)
                .timeoutSeconds(30)
                .retryCount(1)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(summarizeCommand);
    }

    private void createGenerateCommand(Bot bot) {
        Command generateCommand = Command.builder()
                .bot(bot)
                .command("/generate")
                .responseTemplate("✨ Generated Content: {ai_generated}")
                .isEnabled(true)
                .description("Generate content using AI")
                .type(Command.CommandType.GENERATION)
                .trigger(Command.TriggerType.MANUAL)
                .priority(12)
                .timeoutSeconds(30)
                .retryCount(1)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(generateCommand);
    }

    private void createAnalyzeCommand(Bot bot) {
        Command analyzeCommand = Command.builder()
                .bot(bot)
                .command("/analyze")
                .responseTemplate("🔍 Analysis Results:\n{ai_analysis}")
                .isEnabled(true)
                .description("Analyze text using AI")
                .type(Command.CommandType.ANALYSIS)
                .trigger(Command.TriggerType.MANUAL)
                .priority(13)
                .timeoutSeconds(30)
                .retryCount(1)
                .createdBy("system")
                .updatedBy("system")
                .build();

        commandRepository.save(analyzeCommand);
    }

    /**
     * Remove all default commands for a bot
     */
    public void removeDefaultCommands(Long botId) {
        log.info("Removing default commands for bot: {}", botId);

        List<String> defaultCommands = List.of("/start", "/help", "/status", "/ping", "/info", "/settings",
                "/ai", "/summarize", "/generate", "/analyze");

        for (String commandName : defaultCommands) {
            commandRepository.findByBotIdAndCommand(botId, commandName)
                    .ifPresent(commandRepository::delete);
        }

        log.info("Removed default commands for bot: {}", botId);
    }
}
