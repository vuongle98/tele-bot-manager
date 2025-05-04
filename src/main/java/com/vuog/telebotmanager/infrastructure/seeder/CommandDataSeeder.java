package com.vuog.telebotmanager.infrastructure.seeder;

import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.BotCommandRepository;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.CommandRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class that seeds default bot commands when the application starts.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CommandDataSeeder {

    private final TelegramBotRepository botRepository;
    private final BotCommandRepository commandRepository;
    private final CommandRegistryService commandRegistryService;

    @Bean
    @Transactional
    public CommandLineRunner seedCommands() {
        return args -> {
            seedDefaultCommands();
        };
    }

    public void seedDefaultCommands() {
        List<TelegramBot> bots = botRepository.findAll();
        log.info("Checking command data for {} bots", bots.size());

        for (TelegramBot bot : bots) {
            // Check if the bot already has commands
            List<BotCommand> existingCommands = commandRepository.findAllByBotId(bot.getId());
            if (existingCommands.isEmpty()) {
                log.info("No commands found for bot: {} (ID: {}). Adding default commands.", bot.getName(), bot.getId());
                List<BotCommand> defaultCommands = createDefaultCommands(bot);
                commandRepository.saveAll(defaultCommands);
                log.info("Added {} default commands for bot: {}", defaultCommands.size(), bot.getName());
                registerCommandsToRegistry(defaultCommands);
            } else {
                log.info("Bot {} already has {} commands. Skipping seed.", bot.getName(), existingCommands.size());
                registerCommandsToRegistry(existingCommands);
            }
        }
    }

    /**
     * Registers all bot commands to the command registry
     *
     * @param commands The commands to register
     */
    private void registerCommandsToRegistry(List<BotCommand> commands) {
        log.info("Registering {} commands to command registry", commands.size());
        commandRegistryService.registerCommands(commands);
    }

    /**
     * Creates a list of default commands for a bot
     */
    private List<BotCommand> createDefaultCommands(TelegramBot bot) {
        List<BotCommand> commands = new ArrayList<>();

        // Weather command
        BotCommand weatherCommand = new BotCommand();
        weatherCommand.setBot(bot);
        weatherCommand.setCommand("weather");
        weatherCommand.setDescription("Get weather information for a location");
        weatherCommand.setResponseTemplate("Weather information for {{args}}:\n\n🌡️ Temperature: 25°C\n💨 Wind: 5 km/h\n💧 Humidity: 65%\n\nNote: This is a demo response. Integrate with a weather API for real data.");
        weatherCommand.setIsEnabled(true);
        weatherCommand.setHandlerMethod("showWeather");
        commands.add(weatherCommand);

        // Quote command
        BotCommand quoteCommand = new BotCommand();
        quoteCommand.setBot(bot);
        quoteCommand.setCommand("quote");
        quoteCommand.setDescription("Get a random inspirational quote");
        quoteCommand.setResponseTemplate("\"The only way to do great work is to love what you do.\" — Steve Jobs\n\nRequested by: {{firstName}} {{lastName}}");
        quoteCommand.setIsEnabled(true);
        commands.add(quoteCommand);

        // Echo command
        BotCommand echoCommand = new BotCommand();
        echoCommand.setBot(bot);
        echoCommand.setCommand("echo");
        echoCommand.setDescription("Echo back your message");
        echoCommand.setResponseTemplate("You said: {{args}}");
        echoCommand.setIsEnabled(true);
        commands.add(echoCommand);

        // Joke command
        BotCommand jokeCommand = new BotCommand();
        jokeCommand.setBot(bot);
        jokeCommand.setCommand("joke");
        jokeCommand.setDescription("Get a random joke");
        jokeCommand.setResponseTemplate("Why don't scientists trust atoms?\nBecause they make up everything! 😄");
        jokeCommand.setIsEnabled(true);
        commands.add(jokeCommand);

        // Custom greeting command
        BotCommand greetingCommand = new BotCommand();
        greetingCommand.setBot(bot);
        greetingCommand.setCommand("hello");
        greetingCommand.setDescription("Get a personalized greeting");
        greetingCommand.setResponseTemplate("Hello, {{firstName}}! Welcome to {{botName}}. How can I help you today?");
        greetingCommand.setIsEnabled(true);
        commands.add(greetingCommand);

        return commands;
    }
}
