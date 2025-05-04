package com.vuog.telebotmanager.application.usecases;

import com.vuog.telebotmanager.domain.bot.model.BotHistory;
import com.vuog.telebotmanager.interfaces.dto.response.BotHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BotHistoryUseCase {

    Page<BotHistoryResponseDto> getAllByBot(Long botId, Pageable pageable);

    Page<BotHistoryResponseDto> getAll(Pageable pageable);

    List<BotHistoryResponseDto> getAllByBot(Long botId);
}
