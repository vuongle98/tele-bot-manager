package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ScheduledMessageRepository {
    
    /**
     * Find all pending messages for a specific bot
     */
    List<ScheduledMessage> findAllByBotIdAndIsSentFalseAndIsCancelledFalse(Long botId);
    
    /**
     * Find all non-cancelled messages for a specific bot ordered by scheduled time
     */
    List<ScheduledMessage> findAllByBotIdAndIsCancelledFalseOrderByScheduledTimeAsc(Long botId);
    
    /**
     * Find all pending messages for a specific bot ordered by scheduled time
     */
    List<ScheduledMessage> findAllByBotIdAndIsSentFalseAndIsCancelledFalseOrderByScheduledTimeAsc(Long botId);
    
    /**
     * Find all messages that need to be sent (not sent, not cancelled, and scheduled time <= now)
     */
    List<ScheduledMessage> findAllByIsSentFalseAndIsCancelledFalseAndIsCancelledFalseAndScheduledTimeLessThanEqual(LocalDateTime now);

    List<ScheduledMessage> findByBot(TelegramBot bot);

    List<ScheduledMessage> findByBotId(Long botId);

    ScheduledMessage save(ScheduledMessage message);

    void delete(ScheduledMessage message);

    Optional<ScheduledMessage> findById(Long id);
}