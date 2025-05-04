package com.vuog.telebotmanager.domain.bot.repository;

import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.common.enums.CommonEnum.BotStatus;
import com.vuog.telebotmanager.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;


public interface TelegramBotRepository {
    
    // Find by API token (secured)
    Optional<TelegramBot> findByApiToken(String apiToken);
    
    // Find all bots by owner
    List<TelegramBot> findByOwner(User owner);
    
    // Find all bots by status
    List<TelegramBot> findAllByStatus(BotStatus status);
    
    // Find by name and owner (for uniqueness check)
    Optional<TelegramBot> findByNameAndOwner(String name, User owner);

    // Count bots by status
    long countByStatus(BotStatus status);

    
    // Check if bot exists and is owned by user
    boolean existsByIdAndOwner(Long id, User owner);

    Page<TelegramBot> findAll(Specification<TelegramBot> spec, Pageable pageable);

    Page<TelegramBot> findAll(Pageable pageable);

    List<TelegramBot> findAll();

    long count();

    long countByNameAndOwner(String name, User owner);

    long countByName(String name);

    TelegramBot save(TelegramBot bot);

    Optional<TelegramBot> findById(Long id);

    void delete(TelegramBot bot);

    void deleteById(Long id);

    boolean existsById(Long id);
}