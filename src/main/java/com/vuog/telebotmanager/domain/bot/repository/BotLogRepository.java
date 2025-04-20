package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.BotLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotLogRepository extends JpaRepository<BotLog, Long> {
}
