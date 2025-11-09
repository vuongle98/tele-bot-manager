package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.BotRuntimeState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotRuntimeStateRepository extends JpaRepository<BotRuntimeState, Long> {
    List<BotRuntimeState> findByIsRunningTrue();
}
