package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.command.CreateBotRequest;
import com.vuog.telebotmanager.application.command.ScheduleMessageRequest;
import com.vuog.telebotmanager.application.command.UpdateBotConfigRequest;
import com.vuog.telebotmanager.application.dto.BotStatusResponse;
import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public interface BotService {

    TelegramBot createBot(CreateBotRequest request);

    void startBot(Long botId) throws TelegramApiException;

    void stopBot(Long botId) throws TelegramApiException;

    void deleteBot(Long botId);

//    void updateBot(Long botId, CreateBotRequest request);

    void updateBotConfiguration(Long botId, UpdateBotConfigRequest request) throws TelegramApiException;

    List<BotStatusResponse> getAllBotStatuses();

    void restartBot(Long botId) throws TelegramApiException;

    ScheduledMessage scheduleMessage(Long botId, ScheduleMessageRequest request);

    List<ScheduledMessage> getScheduledMessages(Long botId);

    void cancelScheduledMessage(Long botId);
}
