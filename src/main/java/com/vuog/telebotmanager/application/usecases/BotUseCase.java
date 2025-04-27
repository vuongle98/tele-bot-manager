package com.vuog.telebotmanager.application.usecases;

import com.vuog.telebotmanager.application.command.CreateBotCommand;
import com.vuog.telebotmanager.application.command.UpdateBotConfigCommand;
import com.vuog.telebotmanager.application.dto.BotStatusResponse;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public interface BotUseCase {

    TelegramBot createBot(CreateBotCommand request);

    void startBot(Long botId) throws TelegramApiException;

    void stopBot(Long botId) throws TelegramApiException;

    void deleteBot(Long botId);

//    void updateBot(Long botId, CreateBotRequest request);

    void updateBotConfiguration(Long botId, UpdateBotConfigCommand request) throws TelegramApiException;

    List<BotStatusResponse> getAllBotStatuses();

    void restartBot(Long botId) throws TelegramApiException;
}
