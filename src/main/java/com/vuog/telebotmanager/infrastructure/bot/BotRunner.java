package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.application.service.CommandHandlerServiceImpl;
import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.handler.BotHandlerFactory;
import com.vuog.telebotmanager.infrastructure.bot.handler.LongPollingBotHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotRunner implements BotHandlerRegistry {
    private final Map<Long, BotInstance> runningBots = new ConcurrentHashMap<>();
    private final TelegramBotsApi botsApi;
    private final TelegramBotRepository botRepository;
    private final BotHandlerFactory botHandlerFactory;

    private final ApplicationContext applicationContext;

    private CommandHandlerServiceImpl commandHandlerServiceImpl;

    public synchronized void startBot(TelegramBot bot) throws TelegramApiException {
        if (runningBots.containsKey(bot.getId())) {
            throw new IllegalStateException("Bot " + bot.getId() + " is already running");
        }

        try {
            BotHandler handler = createHandler(bot);

            if (handler instanceof WebhookBotBase webhookBot) {
                SetWebhook setWebhook = SetWebhook.builder()
                    .url(bot.getConfiguration().getWebhookUrl())
                    .build();
                botsApi.registerBot(webhookBot, setWebhook);
                runningBots.put(bot.getId(), new BotInstance(null, handler));
            } else if (handler instanceof LongPollingBotBase pollingBot) {
                BotSession session = DefaultBotSession.class.getDeclaredConstructor().newInstance();
                session.setToken(pollingBot.getBot().getApiToken());
                session.setCallback(pollingBot);
                session.setOptions(pollingBot.getOptions());
                session.start();
                runningBots.put(bot.getId(), new BotInstance(session, handler));
            } else {
                throw new IllegalStateException("Unsupported bot handler type");
            }

            log.info("Started bot {} ({} - {})",
                bot.getId(), bot.getName(), bot.getConfiguration().getUpdateMethod());
        } catch (Exception e) {
            log.error("Failed to start bot {}", bot.getId(), e); // Changed to log full stack trace
            throw new TelegramApiException("Failed to start bot", e);
        }
    }

    public BotHandler getHandler(Long botId) {
        BotInstance instance = runningBots.get(botId);
        if (instance == null) {
            throw new IllegalStateException("Bot " + botId + " is not running");
        }
        return instance.getHandler();
    }

    @Override
    public void registerHandler(Long botId, BotHandler handler) {
        runningBots.put(botId, new BotInstance(null, handler));
    }

    @Override
    public void unregisterHandler(Long botId) {
        runningBots.remove(botId);
    }

    public synchronized void stopBot(Long botId) {
        BotInstance instance = runningBots.get(botId);
        if (instance != null) {
            try {
                if (instance.session() != null) { // Check null for webhook bots
                    instance.session().stop();
                }
                log.info("Stopped bot {}", botId);
            } catch (Exception e) {
                log.error("Error stopping bot {}", botId, e); // log full exception
            } finally {
                runningBots.remove(botId);
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    public void monitorBotStates() {
        runningBots.forEach((id, instance) -> {
            if (instance.session() != null && !instance.session().isRunning()) {
                log.warn("Bot {} crashed", id);
                handleCrashedBot(id);
            }
        });
    }

    private BotHandler createHandler(TelegramBot bot) {

        BotHandler handler = botHandlerFactory.createHandler(bot);

        if (handler instanceof LongPollingBotHandler pollingHandler) {

            if (commandHandlerServiceImpl == null) {
                commandHandlerServiceImpl = applicationContext.getBean(CommandHandlerServiceImpl.class);
            }
            pollingHandler.setCommandHandlerServiceImpl(commandHandlerServiceImpl);
        }

        return handler;
    }

    private void handleCrashedBot(Long botId) {
        runningBots.remove(botId);
        botRepository.findById(botId).ifPresent(bot -> {
            bot.setStatus(CommonEnum.BotStatus.ERRORED);
            botRepository.save(bot);
            log.warn("Marked bot {} as errored", botId);
        });
    }

    public record BotInstance(BotSession session, BotHandler handler) {
        public BotHandler getHandler() {
            return handler;
        }
    }
}