package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.usecases.AlertNotificationUseCase;
import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.common.exception.BotNotFoundException;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.BotHandler;
import com.vuog.telebotmanager.infrastructure.bot.BotHandlerRegistry;
import com.vuog.telebotmanager.infrastructure.bot.LongPollingBotBase;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Slf4j
@Service
@RequiredArgsConstructor
public class AlertNotificationUseCaseImpl implements AlertNotificationUseCase {

    private final TelegramBotRepository botRepository;

    @Lazy
    private final BotHandlerRegistry botHandlerRegistry;

    @Override
    public void sendAlert(Long botId, String chatId, String message, CommonEnum.AlertLevel level) {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        String prefix = switch (level) {
            case INFO -> "ℹ️ INFO: ";
            case WARNING -> "⚠️ WARNING: ";
            case ERROR -> "🚨 ERROR: ";
            case SUCCESS -> "✅ SUCCESS: ";
        };

        String fullMessage = prefix + message;

        try {
            BotHandler handler = botHandlerRegistry.getHandler(botId);
            SendMessage sendMessage = new SendMessage(chatId, fullMessage);

            if (handler instanceof WebhookBotBase webhookBot) {
                webhookBot.execute(sendMessage);
            } else if (handler instanceof LongPollingBotBase pollingBot) {
                pollingBot.execute(sendMessage);
            }

            log.info("Sent alert to chat {} using bot {}: {}", chatId, bot.getName(), fullMessage);
        } catch (Exception e) {
            log.error("Failed to send alert to chat {} using bot {}: {}", chatId, bot.getName(), message, e);
        }
    }

    @Override
    public void sendInfo(Long botId, String chatId, String message) {
        sendAlert(botId, chatId, message, CommonEnum.AlertLevel.INFO);
    }

    @Override
    public void sendWarning(Long botId, String chatId, String message) {
        sendAlert(botId, chatId, message, CommonEnum.AlertLevel.WARNING);
    }

    @Override
    public void sendError(Long botId, String chatId, String message) {
        sendAlert(botId, chatId, message, CommonEnum.AlertLevel.ERROR);
    }

    @Override
    public void sendSuccess(Long botId, String chatId, String message) {
        sendAlert(botId, chatId, message, CommonEnum.AlertLevel.SUCCESS);
    }
}
