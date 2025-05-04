package com.vuog.telebotmanager.infrastructure.bot.handler;

import com.vuog.telebotmanager.application.service.CommandHandlerServiceImpl;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.infrastructure.bot.LongPollingBotBase;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Setter
public class LongPollingBotHandler extends LongPollingBotBase {
    private CommandHandlerServiceImpl commandHandlerServiceImpl;

    public LongPollingBotHandler(TelegramBot bot) {
        super(bot);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            if (text.startsWith("/") && commandHandlerServiceImpl != null) {
                commandHandlerServiceImpl.handleUpdate(update, bot.getId());
            }
            // handleMessage(message);
            // Handle other message types here, if needed
            // For example, you can send a message to a group chat
            // execute(new SendMessage(message.getChatId(), "Message received: " + message.getText()));
            // You can also send a message to a specific user
            // execute(new SendMessage(message.getChatId(), "Message received: " + message.getText()).setReplyToMessageId(message.getMessageId()));
            // Or send a message to a specific chat using the chat id
            // execute(new SendMessage(chatId, "Message received: " + message.getText()));
            // You can also send a message to a specific user using the user id
            // execute(new SendMessage(userId, "Message received: " + message.getText()));
        }
    }

    @Override
    public AbsSender getSender() {
        return this;
    }

    @Override
    public Long getBotId() {
        return bot.getId();
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