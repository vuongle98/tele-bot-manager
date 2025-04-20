package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessage, Long> {

    List<ScheduledMessage> findByBotAndScheduledTimeBeforeAndIsSentFalse(TelegramBot bot, LocalDateTime now);

    @Query("SELECT m FROM ScheduledMessage m WHERE m.scheduledTime <= :currentTime AND m.isSent = false")
    List<ScheduledMessage> findDueMessages(@Param("currentTime") LocalDateTime currentTime);

    List<ScheduledMessage> findByBot(TelegramBot bot);

    List<ScheduledMessage> findByBotId(Long botId);
}