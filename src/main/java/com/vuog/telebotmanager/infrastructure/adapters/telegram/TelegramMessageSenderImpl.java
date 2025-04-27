package com.vuog.telebotmanager.infrastructure.adapters.telegram;

import com.vuog.telebotmanager.domain.bot.ports.TelegramMessageSender;
import com.vuog.telebotmanager.infrastructure.bot.BotHandler;
import com.vuog.telebotmanager.infrastructure.bot.BotRunner;
import com.vuog.telebotmanager.infrastructure.bot.LongPollingBotBase;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageSenderImpl implements TelegramMessageSender {
    private final BotRunner botRunner;
    
    @Override
    public boolean sendMessage(Long botId, String chatId, String message) {
        BotHandler handler = botRunner.getHandler(botId);
        if (handler == null) {
            log.error("No bot handler found for bot id {}", botId);
            return false;
        }

        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.enableHtml(true);
        
        try {
            // Send message via appropriate bot type
            if (handler instanceof WebhookBotBase webhookBot) {
                webhookBot.execute(sendMessage);
                return true;
            } else if (handler instanceof LongPollingBotBase pollingBot) {
                pollingBot.execute(sendMessage);
                return true;
            } else {
                log.error("Unknown bot handler type for bot id {}", botId);
                return false;
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send message via Telegram API", e);
            return false;
        }
    }
}
