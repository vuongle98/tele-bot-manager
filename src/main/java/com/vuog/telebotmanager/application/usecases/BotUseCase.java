package com.vuog.telebotmanager.application.usecases;

import com.vuog.telebotmanager.application.command.CreateBotCommand;
import com.vuog.telebotmanager.application.command.UpdateBotConfigCommand;
import com.vuog.telebotmanager.application.dto.BotResponseDto;
import com.vuog.telebotmanager.application.dto.BotStatusResponse;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.interfaces.dto.query.BotQuery;
import com.vuog.telebotmanager.interfaces.dto.request.UpdateBotRequest;
import com.vuog.telebotmanager.interfaces.dto.response.BotDetailResponseDto;
import com.vuog.telebotmanager.interfaces.rest.dto.BotStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<BotResponseDto> getBots(BotQuery query, Pageable pageable);

    BotDetailResponseDto getBotDetails(Long id);

    BotResponseDto updateBot(Long id, UpdateBotRequest request);

    void refreshBotStatus(Long id);

    BotStatistic statistics();
}
