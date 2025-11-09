package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.BotHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BotHistory entity
 * Follows Clean Architecture by defining repository contracts in domain layer
 */
@Repository
public interface BotHistoryRepository extends JpaRepository<BotHistory, Long> {

    /**
     * Find all history records for a bot, ordered by timestamp descending
     */
    @Query("SELECT h FROM BotHistory h WHERE h.bot.id = :botId ORDER BY h.timestamp DESC")
    List<BotHistory> findByBotIdOrderByTimestampDesc(@Param("botId") Long botId);


    @Query("SELECT h FROM BotHistory h WHERE h.bot.id = :botId ORDER BY h.timestamp DESC")
    Page<BotHistory> findByBotIdOrderByTimestampDesc(@Param("botId") Long botId, Pageable pageable);

    /**
     * Find history records for a bot
     */
    List<BotHistory> findByBotId(Long botId);
}
