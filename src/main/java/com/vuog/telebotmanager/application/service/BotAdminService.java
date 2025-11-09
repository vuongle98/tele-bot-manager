package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotAdminService {

    private final BotRepository botRepository;

    @Transactional(readOnly = true)
    public Page<Bot> listBots(Pageable pageable) {
        return botRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Bot> getBot(Long botId) {
        return botRepository.findById(botId);
    }

    @Transactional
    public void activate(Long botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));
        bot.activate();
        botRepository.save(bot);
    }

    @Transactional
    public void deactivate(Long botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));
        bot.deactivate();
        botRepository.save(bot);
    }

    @Transactional
    public void delete(Long botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + botId));
        botRepository.delete(bot);
    }
}
