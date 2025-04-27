package com.vuog.telebotmanager.infrastructure.adapters.persistence;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.domain.bot.repository.TelegramBotRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTelegramBotRepository extends
        JpaRepository<TelegramBot, Long>,
        TelegramBotRepository {
}
