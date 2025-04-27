package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.usecases.BotBroadcastUseCase;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import com.vuog.telebotmanager.infrastructure.bot.BotRunner;
import com.vuog.telebotmanager.infrastructure.bot.BotHandler;
import com.vuog.telebotmanager.infrastructure.bot.LongPollingBotBase;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import com.vuog.telebotmanager.common.exception.BotNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotBroadcastServiceImpl implements BotBroadcastUseCase {
    private final TelegramBotRepository botRepository;
    private final BotRunner botRunner;

    public void broadcastMessage(Long botId, List<String> chatIds, String message) {
        TelegramBot bot = botRepository.findById(botId)
                .orElseThrow(() -> new BotNotFoundException(botId));

        BotHandler handler = botRunner.getHandler(botId);

        for (String chatId : chatIds) {
            try {
                SendMessage sendMessage = new SendMessage(chatId, message);
                if (handler instanceof WebhookBotBase webhookBot) {
                    webhookBot.execute(sendMessage);
                } else if (handler instanceof LongPollingBotBase pollingBot) {
                    pollingBot.execute(sendMessage);
                }
                log.info("Broadcasted to {} from bot {}", chatId, bot.getName());
            } catch (Exception e) {
                log.error("Failed to send to chat {} using bot {}", chatId, bot.getName(), e);
            }
        }
    }
}
