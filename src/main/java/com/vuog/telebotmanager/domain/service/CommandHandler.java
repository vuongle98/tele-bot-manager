package com.vuog.telebotmanager.domain.service;

import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import java.util.Collections;
import java.util.List;

/**
 * Domain service interface for command handling
 * Follows Clean Architecture by defining service contracts in domain layer
 */
public interface CommandHandler {

    /**
     * Check if this handler can process the given command
     */
    boolean canHandle(CommandRequest request);

    /**
     * Execute the command and return response
     */
    CommandResponse execute(CommandRequest request);

    /**
     * Get the command type this handler supports
     */
    String getSupportedCommandType();

    /**
     * Get the priority of this handler (lower number = higher priority)
     */
    int getPriority();

    /**
     * Check if handler is available for execution
     */
    boolean isAvailable();

    /**
     * Optional: list of commands supported by this handler (for help/diagnostics)
     */
    default List<String> getSupportedCommands() { return Collections.emptyList(); }
}
