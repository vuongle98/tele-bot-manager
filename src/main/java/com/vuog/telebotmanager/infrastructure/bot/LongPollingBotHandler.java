package com.vuog.telebotmanager.infrastructure.bot;

import com.vuog.telebotmanager.application.service.CommandHandlerService;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Setter
@Slf4j
public class LongPollingBotHandler extends LongPollingBotBase {

    private CommandHandlerService commandHandlerService;

    public LongPollingBotHandler(TelegramBot bot) {
        super(bot);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            try {
                if (text.startsWith("/")) {
                    handleCommand(message);
                    if (commandHandlerService != null) {
                        commandHandlerService.handleUpdate(update, bot.getId());
                    }
                } else {
                    handleMessage(message);
                }
            } catch (TelegramApiException e) {
                log.error("Error processing update: {}", e.getMessage());
            }
        }
    }

    @Override
    public AbsSender getSender() {
        return this;
    }

    @Override
    public Long getBotId() {
        return bot.getId();  // Assuming TelegramBot has an ID field
    }

    private void handleCommand(Message message) throws TelegramApiException {
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId());
        response.setText("Command received: " + message.getText());
        execute(response);
    }

    private void handleMessage(Message message) throws TelegramApiException {
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId());
        response.setText("Message received: " + message.getText());
        execute(response);
    }
}