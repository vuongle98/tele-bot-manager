package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.BotLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotLogRepository extends JpaRepository<BotLog, Long> {
    List<BotLog> findByBotId(Long botId);
    
    @Query("SELECT COUNT(l) FROM BotLog l WHERE l.botId = :botId")
    long countByBotId(@Param("botId") Long botId);
}