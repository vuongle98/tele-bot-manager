package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.usecases.BotLogUseCase;
import com.vuog.telebotmanager.domain.bot.model.BotLog;
import com.vuog.telebotmanager.domain.bot.repository.BotLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class BotLogUseCaseImpl implements BotLogUseCase {


    private final BotLogRepository logRepository;

    @Override
    public void log(Long botId, String chatId, String message) {
        BotLog log = new BotLog();
        log.setBotId(botId);
        log.setChatId(chatId);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}
