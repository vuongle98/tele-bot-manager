package com.vuog.telebotmanager.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.vuog.telebotmanager.application.usecase.CommandManagementUseCase;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.CommandDto;
import com.vuog.telebotmanager.presentation.dto.query.CommandQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreateCommandRequest;
import com.vuog.telebotmanager.presentation.dto.request.ExecuteCommandRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateCommandRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for command management operations
 * Provides API endpoints for command CRUD operations and execution
 */
@RestController
@RequestMapping("/api/v1/commands")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Command Management", description = "API for managing bot commands")
public class CommandController {

    private final CommandManagementUseCase commandManagementUseCase;

    @PostMapping
    @Operation(summary = "Create a new command", description = "Creates a new command for a bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommandDto> createCommand(@Valid @RequestBody CreateCommandRequest request) {
        log.info("Creating command: {} for bot: {}", request.getCommand(), request.getBotId());

        Command command = commandManagementUseCase.createCommand(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommandDto.fromEntity(command));
    }

    @GetMapping("/{commandId}")
    @Operation(summary = "Get command by ID", description = "Retrieves a command by its unique identifier")
    public ResponseEntity<Command> getCommandById(@PathVariable Long commandId) {
        log.info("Getting command by ID: {}", commandId);

        Optional<Command> command = commandManagementUseCase.getCommandById(commandId);
        return command.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all commands", description = "Retrieves all commands with pagination and filtering")
    public ResponseEntity<Page<Command>> getAllCommands(CommandQuery query, Pageable pageable) {
        log.info("Getting all commands with pagination and query: {}", query);

        Page<Command> commands = commandManagementUseCase.findAll(query, pageable);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/bot/{botId}")
    @Operation(summary = "Get commands by bot ID", description = "Retrieves all commands for a specific bot")
    public ResponseEntity<List<Command>> getCommandsByBotId(@PathVariable Long botId) {
        log.info("Getting commands for bot: {}", botId);

        List<Command> commands = commandManagementUseCase.getCommandsByBotId(botId);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/enabled/bot/{botId}")
    @Operation(summary = "Get enabled commands by bot ID", description = "Retrieves all enabled commands for a specific bot")
    public ResponseEntity<List<Command>> getEnabledCommandsByBotId(@PathVariable Long botId) {
        log.info("Getting enabled commands for bot: {}", botId);

        List<Command> commands = commandManagementUseCase.getEnabledCommandsByBotId(botId);
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/ai-powered")
    @Operation(summary = "Get AI-powered commands", description = "Retrieves all AI-powered commands")
    public ResponseEntity<List<Command>> getAiPoweredCommands() {
        log.info("Getting AI-powered commands");

        List<Command> commands = commandManagementUseCase.getAiPoweredCommands();
        return ResponseEntity.ok(commands);
    }

    @GetMapping("/plugin")
    @Operation(summary = "Get plugin commands", description = "Retrieves all plugin commands")
    public ResponseEntity<List<Command>> getPluginCommands() {
        log.info("Getting plugin commands");

        List<Command> commands = commandManagementUseCase.getPluginCommands();
        return ResponseEntity.ok(commands);
    }

    @PutMapping("/{commandId}")
    @Operation(summary = "Update command", description = "Updates command information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Command> updateCommand(@PathVariable Long commandId, @Valid @RequestBody UpdateCommandRequest request) {
        log.info("Updating command: {}", commandId);

        Command command = commandManagementUseCase.updateCommand(
                commandId,
                request
        );

        return ResponseEntity.ok(command);
    }

    @PostMapping("/{commandId}/enable")
    @Operation(summary = "Enable command", description = "Enables a command")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Command> enableCommand(@PathVariable Long commandId) {
        log.info("Enabling command: {}", commandId);

        Command command = commandManagementUseCase.enableCommand(commandId);
        return ResponseEntity.ok(command);
    }

    @PostMapping("/{commandId}/disable")
    @Operation(summary = "Disable command", description = "Disables a command")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Command> disableCommand(@PathVariable Long commandId) {
        log.info("Disabling command: {}", commandId);

        Command command = commandManagementUseCase.disableCommand(commandId);
        return ResponseEntity.ok(command);
    }

    @DeleteMapping("/{commandId}")
    @Operation(summary = "Delete command", description = "Deletes a command")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommand(@PathVariable Long commandId) {
        log.info("Deleting command: {}", commandId);

        commandManagementUseCase.deleteCommand(commandId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/execute")
    @Operation(summary = "Execute command", description = "Executes a command")
    public ResponseEntity<CommandResponse> executeCommand(@Valid @RequestBody ExecuteCommandRequest request) {
        log.info("Executing command: {}", request.getCommand());

        CommandRequest commandRequest = CommandRequest.create(
                request.getCommandId(),
                request.getBotId(),
                request.getUserId(),
                request.getChatId(),
                request.getCommand(),
                request.getInputText()
        );

        CommandResponse response = commandManagementUseCase.executeCommand(commandRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commandId}/statistics")
    @Operation(summary = "Get command statistics", description = "Retrieves statistics for a command")
    public ResponseEntity<CommandManagementUseCase.CommandStatistics> getCommandStatistics(@PathVariable Long commandId) {
        log.info("Getting statistics for command: {}", commandId);

        CommandManagementUseCase.CommandStatistics stats = commandManagementUseCase.getCommandStatistics(commandId);
        return ResponseEntity.ok(stats);
    }
}
