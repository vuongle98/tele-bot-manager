package com.vuog.telebotmanager.application.usecase;

import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.query.CommandQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreateCommandRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateCommandRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Application use case for command management
 * Follows Clean Architecture by defining application layer contracts
 */
public interface CommandManagementUseCase {

    /**
     * Create a new command
     */
    Command createCommand(CreateCommandRequest request);

    /**
     * Update command
     */
    Command updateCommand(Long commandId, UpdateCommandRequest request);

    /**
     * Enable command
     */
    Command enableCommand(Long commandId);

    /**
     * Disable command
     */
    Command disableCommand(Long commandId);

    /**
     * Delete command
     */
    void deleteCommand(Long commandId);

    /**
     * Search commands by query with pagination
     */
    Page<Command> findAll(CommandQuery query, Pageable pageable);

    /**
     * Get command by ID
     */
    Optional<Command> getCommandById(Long commandId);

    /**
     * Get commands by bot ID
     */
    List<Command> getCommandsByBotId(Long botId);

    /**
     * Get enabled commands by bot ID
     */
    List<Command> getEnabledCommandsByBotId(Long botId);

    /**
     * Get commands by type
     */
    List<Command> getCommandsByType(Command.CommandType type);

    /**
     * Get AI-powered commands
     */
    List<Command> getAiPoweredCommands();

    /**
     * Get plugin commands
     */
    List<Command> getPluginCommands();

    /**
     * Get commands with pagination
     */
    Page<Command> getCommands(Pageable pageable);

    /**
     * Get commands by bot ID with pagination
     */
    Page<Command> getCommandsByBotId(Long botId, Pageable pageable);

    /**
     * Execute command
     */
    CommandResponse executeCommand(CommandRequest request);

    /**
     * Get command statistics
     */
    CommandStatistics getCommandStatistics(Long commandId);

    /**
     * Command statistics interface
     */
    interface CommandStatistics {
        Long getCommandId();

        String getCommand();

        long getTotalExecutions();

        long getSuccessfulExecutions();

        long getFailedExecutions();

        double getSuccessRate();

        long getAverageExecutionTime();

        String getLastExecution();

        boolean isEnabled();
    }
}
