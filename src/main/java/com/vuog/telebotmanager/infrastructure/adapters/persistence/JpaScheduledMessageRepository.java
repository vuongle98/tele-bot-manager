package com.vuog.telebotmanager.infrastructure.adapters.persistence;

import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;
import com.vuog.telebotmanager.domain.bot.repository.ScheduledMessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaScheduledMessageRepository extends
        JpaRepository<ScheduledMessage, Long>,
        ScheduledMessageRepository {


}
