package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.BotHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing bot history records
 */
@Repository
public interface BotHistoryRepository extends JpaRepository<BotHistory, Long> {
    
    /**
     * Find all history records for a specific bot
     * 
     * @param botId The ID of the bot
     * @param pageable Pagination information
     * @return Paginated list of history records
     */
    Page<BotHistory> findByBotIdOrderByTimestampDesc(Long botId, Pageable pageable);
    
    /**
     * Find recent history records for a specific bot
     * 
     * @param botId The ID of the bot
     * @param since The timestamp to filter records from
     * @return List of recent history records
     */
    List<BotHistory> findByBotIdAndTimestampAfterOrderByTimestampDesc(Long botId, LocalDateTime since);
    
    /**
     * Find history records for all bots, sorted by timestamp
     * 
     * @param pageable Pagination information
     * @return Paginated list of history records
     */
    Page<BotHistory> findAllByOrderByTimestampDesc(Pageable pageable);

    List<BotHistory> findAllByBotIdOrderByTimestampDesc(Long botId);
}
