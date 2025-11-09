package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.Command;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Command entity
 * Follows Clean Architecture by defining repository contracts in domain layer
 */
@Repository
public interface CommandRepository extends JpaRepository<Command, Long>, BaseQueryRepository<Command, Long> {

    /**
     * Find commands by bot ID
     */
    List<Command> findByBotId(Long botId);

    /**
     * Find commands by bot ID and enabled status
     */
    List<Command> findByBotIdAndIsEnabled(Long botId, Boolean isEnabled);

    /**
     * Find command by bot ID and command name
     */
    Optional<Command> findByBotIdAndCommand(Long botId, String command);

    /**
     * Resolve command by bot or global by name, preferring bot-specific over global
     */
    @Query("SELECT c FROM Command c WHERE c.isEnabled = true AND (c.bot.id = :botId OR c.bot IS NULL) AND c.command = :command ORDER BY CASE WHEN c.bot.id = :botId THEN 0 ELSE 1 END, c.priority ASC")
    List<Command> resolveByBotOrGlobalAndCommand(@Param("botId") Long botId, @Param("command") String command);

    /**
     * Find commands by type
     */
    List<Command> findByType(Command.CommandType type);

    /**
     * Find commands by trigger type
     */
    List<Command> findByTrigger(Command.TriggerType trigger);

    /**
     * Find commands by type and enabled status
     */
    List<Command> findByTypeAndIsEnabled(Command.CommandType type, Boolean isEnabled);

    /**
     * Find commands by plugin name
     */
    List<Command> findByPluginName(String pluginName);

    /**
     * Find enabled commands by bot ID
     */
    @Query("SELECT c FROM Command c WHERE c.bot.id = :botId AND c.isEnabled = true ORDER BY c.priority ASC")
    List<Command> findEnabledCommandsByBotId(@Param("botId") Long botId);

    /**
     * Find enabled global commands (bot is null)
     */
    @Query("SELECT c FROM Command c WHERE c.bot IS NULL AND c.isEnabled = true ORDER BY c.priority ASC")
    List<Command> findEnabledGlobalCommands();

    /**
     * Find enabled commands for a bot including global defaults
     */
    @Query("SELECT c FROM Command c WHERE (c.bot.id = :botId OR c.bot IS NULL) AND c.isEnabled = true ORDER BY c.priority ASC")
    List<Command> findEnabledCommandsByBotIdOrGlobal(@Param("botId") Long botId);

    /**
     * Find AI-powered commands
     */
    @Query("SELECT c FROM Command c WHERE c.type IN ('AI_TASK', 'AI_ANSWER', 'SUMMARY', 'GENERATION', 'ANALYSIS') AND c.isEnabled = true")
    List<Command> findAiPoweredCommands();

    /**
     * Find commands requiring plugin execution
     */
    @Query("SELECT c FROM Command c WHERE c.type = 'PLUGIN' AND c.pluginName IS NOT NULL AND c.isEnabled = true")
    List<Command> findPluginCommands();

    /**
     * Find commands by bot ID with pagination
     */
    Page<Command> findByBotId(Long botId, Pageable pageable);

    /**
     * Find commands by type with pagination
     */
    Page<Command> findByType(Command.CommandType type, Pageable pageable);

    /**
     * Count commands by bot ID
     */
    long countByBotId(Long botId);

    /**
     * Count commands by type
     */
    long countByType(Command.CommandType type);

    /**
     * Count enabled commands by bot ID
     */
    @Query("SELECT COUNT(c) FROM Command c WHERE c.bot.id = :botId AND c.isEnabled = true")
    long countEnabledCommandsByBotId(@Param("botId") Long botId);

    /**
     * Find commands by priority range
     */
    @Query("SELECT c FROM Command c WHERE c.priority BETWEEN :minPriority AND :maxPriority AND c.isEnabled = true ORDER BY c.priority ASC")
    List<Command> findByPriorityRange(@Param("minPriority") Integer minPriority,
                                      @Param("maxPriority") Integer maxPriority);

    /**
     * Find commands created by user
     */
    Page<Command> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * Check if command exists for bot
     */
    boolean existsByBotIdAndCommand(Long botId, String command);
}
