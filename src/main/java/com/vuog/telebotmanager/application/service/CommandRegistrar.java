package com.vuog.telebotmanager.application.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandRegistrar {

    private final CommandRegistry commandRegistry;

    @PostConstruct
    public void registerCommands() {
        commandRegistry.registerCommand("start", context ->
                context.reply("👋 Welcome! I'm your friendly bot.")
        );

        commandRegistry.registerCommand("help", context -> {
            String helpText = """
                    🤖 Available Commands:
                    /start - Start the bot
                    /help - Show help
                    /menu - Show reply keyboard
                    """;

            context.reply(helpText);
        });

        commandRegistry.registerCommand("echo", ctx ->
                ctx.reply("🔁 You said: " + ctx.args())
        );
    }
}