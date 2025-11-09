package com.vuog.telebotmanager.presentation.controller;

import com.vuog.telebotmanager.application.service.BotLifecycleService;
import com.vuog.telebotmanager.application.service.BotOrchestrationService;
import com.vuog.telebotmanager.application.usecase.BotManagementUseCase;
import com.vuog.telebotmanager.application.usecase.CommandManagementUseCase;
import com.vuog.telebotmanager.application.usecase.PluginManagementUseCase;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotHistory;
import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.BotDto;
import com.vuog.telebotmanager.presentation.dto.BotHistoryDto;
import com.vuog.telebotmanager.presentation.dto.BotPluginDto;
import com.vuog.telebotmanager.presentation.dto.CommandDto;
import com.vuog.telebotmanager.presentation.dto.query.BotQuery;
import com.vuog.telebotmanager.presentation.dto.query.CommandQuery;
import com.vuog.telebotmanager.presentation.dto.query.PluginQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreateBotRequest;
import com.vuog.telebotmanager.presentation.dto.request.CreateCommandRequest;
import com.vuog.telebotmanager.presentation.dto.request.CreatePluginRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateBotRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdateCommandRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdatePluginSourceRequest;
import com.vuog.telebotmanager.presentation.dto.request.ProcessMessageRequest;
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
 * REST controller for bot management operations
 * Provides API endpoints for bot CRUD operations and message processing
 */
@RestController
@RequestMapping("/api/v1/bots")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bot Management", description = "API for managing Telegram bots")
public class BotController {

    private final BotManagementUseCase botManagementUseCase;
    private final BotOrchestrationService botOrchestrationService;
    private final BotLifecycleService botLifecycleService;
    private final CommandManagementUseCase commandManagementUseCase;
    private final PluginManagementUseCase pluginManagementUseCase;

    @PostMapping
    @Operation(summary = "Create a new bot", description = "Creates a new Telegram bot with the provided configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotDto> createBot(@Valid @RequestBody CreateBotRequest request) {
        log.info("Creating bot with username: {}", request.getBotUsername());

        Bot bot = botManagementUseCase.createBot(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(BotDto.fromEntity(bot));
    }

    @GetMapping("/{botId}")
    @Operation(summary = "Get bot by ID", description = "Retrieves a bot by its unique identifier")
    public ResponseEntity<BotDto> getBotById(@PathVariable Long botId) {
        log.info("Getting bot by ID: {}", botId);

        Optional<Bot> bot = botManagementUseCase.getBotById(botId);
        return bot.map(b -> ResponseEntity.ok(BotDto.fromEntity(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all bots", description = "Retrieves all bots with pagination")
    public ResponseEntity<Page<BotDto>> getAllBots(BotQuery query, Pageable pageable) throws InterruptedException {
        log.info("Getting all bots with pagination");

        Thread.sleep(500);

        Page<Bot> bots = botManagementUseCase.findAll(query, pageable);
        return ResponseEntity.ok(bots.map(BotDto::fromEntity));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active bots", description = "Retrieves all active bots")
    public ResponseEntity<List<BotDto>> getActiveBots() {
        log.info("Getting active bots");

        List<Bot> bots = botManagementUseCase.getActiveBots();
        return ResponseEntity.ok(bots.stream().map(BotDto::fromEntity).toList());
    }

    @GetMapping("/operational")
    @Operation(summary = "Get operational bots", description = "Retrieves all operational bots")
    public ResponseEntity<List<Bot>> getOperationalBots() {
        log.info("Getting operational bots");

        List<Bot> bots = botManagementUseCase.getOperationalBots();
        return ResponseEntity.ok(bots);
    }

    @PutMapping("/{botId}")
    @Operation(summary = "Update bot", description = "Updates bot information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bot> updateBot(@PathVariable Long botId, @Valid @RequestBody UpdateBotRequest request) {
        log.info("Updating bot with ID: {}", botId);

        Bot bot = botManagementUseCase.updateBot(botId, request);

        return ResponseEntity.ok(bot);
    }

    @PostMapping("/{botId}/activate")
    @Operation(summary = "Activate bot", description = "Activates a bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bot> activateBot(@PathVariable Long botId) {
        log.info("Activating bot with ID: {}", botId);

        Bot bot = botManagementUseCase.activateBot(botId);
        return ResponseEntity.ok(bot);
    }

    @PostMapping("/{botId}/deactivate")
    @Operation(summary = "Deactivate bot", description = "Deactivates a bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bot> deactivateBot(@PathVariable Long botId) {
        log.info("Deactivating bot with ID: {}", botId);

        Bot bot = botManagementUseCase.deactivateBot(botId);
        return ResponseEntity.ok(bot);
    }

    @DeleteMapping("/{botId}")
    @Operation(summary = "Delete bot", description = "Deletes a bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBot(@PathVariable Long botId) {
        log.info("Deleting bot with ID: {}", botId);

        botManagementUseCase.deleteBot(botId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{botId}/process-message")
    @Operation(summary = "Process message", description = "Processes an incoming message for a bot")
    public ResponseEntity<CommandResponse> processMessage(@PathVariable Long botId, @Valid @RequestBody ProcessMessageRequest request) {
        log.info("Processing message for bot: {}", botId);

        CommandRequest commandRequest = CommandRequest.create(
                request.getCommandId(),
                botId.toString(),
                request.getUserId(),
                request.getChatId(),
                request.getCommand(),
                request.getInputText()
        );

        CommandResponse response = botManagementUseCase.processMessage(commandRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{botId}/statistics")
    @Operation(summary = "Get bot statistics", description = "Retrieves statistics for a bot")
    public ResponseEntity<BotManagementUseCase.BotStatistics> getBotStatistics(@PathVariable Long botId) {
        log.info("Getting statistics for bot: {}", botId);

        BotManagementUseCase.BotStatistics stats = botManagementUseCase.getBotStatistics(botId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get all bots statistics", description = "Retrieves aggregated statistics for all bots")
    public ResponseEntity<BotManagementUseCase.AllBotsStatistics> getAllBotsStatistics() {
        log.info("Getting statistics for all bots");

        BotManagementUseCase.AllBotsStatistics stats = botManagementUseCase.getAllBotsStatistics();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{botId}/start")
    @Operation(summary = "Start bot", description = "Starts a bot and loads all its commands and plugins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotDto> startBot(@PathVariable Long botId) {
        log.info("Starting bot: {}", botId);

        Bot bot = botOrchestrationService.startBot(botId);
        return ResponseEntity.ok(BotDto.fromEntity(bot));
    }

    @PostMapping("/{botId}/stop")
    @Operation(summary = "Stop bot", description = "Stops a bot and unloads all its commands and plugins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotDto> stopBot(@PathVariable Long botId) {
        log.info("Stopping bot: {}", botId);

        Bot bot = botOrchestrationService.stopBot(botId);
        return ResponseEntity.ok(BotDto.fromEntity(bot));
    }

    @PostMapping("/{botId}/restart")
    @Operation(summary = "Restart bot", description = "Restarts a bot (stop and start)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotDto> restartBot(@PathVariable Long botId) {
        log.info("Restarting bot: {}", botId);

        Bot bot = botOrchestrationService.restartBot(botId);
        return ResponseEntity.ok(BotDto.fromEntity(bot));
    }

    @GetMapping("/{botId}/status")
    @Operation(summary = "Get bot status", description = "Retrieves detailed status information for a bot")
    public ResponseEntity<BotLifecycleService.BotStatusInfo> getBotStatus(@PathVariable Long botId) {
        log.info("Getting status for bot: {}", botId);

        BotLifecycleService.BotStatusInfo status = botOrchestrationService.getBotStatus(botId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{botId}/commands")
    @Operation(summary = "Get bot commands", description = "Retrieves all commands for a bot with pagination")
    public ResponseEntity<Page<CommandDto>> getBotCommands(
            @PathVariable Long botId,
            CommandQuery query,
            Pageable pageable) {
        log.info("Getting commands for bot: {} with query and pagination", botId);

        // If query is null, create a default query filtering by botId
        if (query == null) {
            query = new CommandQuery();
        }
        query.setBotId(botId);

        Page<Command> commands = commandManagementUseCase.findAll(query, pageable);
        Page<CommandDto> commandDtos = commands.map(CommandDto::fromEntity);

        return ResponseEntity.ok(commandDtos);
    }

    @PostMapping("/{botId}/reload-commands")
    @Operation(summary = "Reload bot commands", description = "Reloads all commands for a bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reloadBotCommands(@PathVariable Long botId) {
        log.info("Reloading commands for bot: {}", botId);

        botLifecycleService.reloadBotCommands(botId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{botId}/reload-plugins")
    @Operation(summary = "Reload bot plugins", description = "Reloads all plugins for a bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reloadBotPlugins(@PathVariable Long botId) {
        log.info("Reloading plugins for bot: {}", botId);

        botLifecycleService.reloadBotPlugins(botId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{botId}/history")
    @Operation(summary = "Get bot history", description = "Retrieves the history of status changes for a bot")
    public ResponseEntity<Page<BotHistoryDto>> getBotHistory(@PathVariable Long botId, Pageable pageable) {
        log.info("Getting history for bot: {}", botId);

        Page<BotHistory> history = botManagementUseCase.getBotHistory(botId, pageable);
        Page<BotHistoryDto> historyDtos = history
                .map(BotHistoryDto::fromEntity);
        return ResponseEntity.ok(historyDtos);
    }

    @GetMapping("/{botId}/plugins")
    @Operation(summary = "Get bot plugins", description = "Retrieves loaded plugins for a bot with pagination")
    public ResponseEntity<Page<BotPluginDto>> getBotPlugins(
            @PathVariable Long botId,
            Pageable pageable) {
        log.info("Getting plugins for bot: {} with pagination", botId);

        List<BotPlugin> plugins = botLifecycleService.getBotPlugins(botId);
        
        // Convert to page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), plugins.size());
        List<BotPlugin> paginatedPlugins = plugins.subList(start, end);
        
        Page<BotPluginDto> pluginDtos = new org.springframework.data.domain.PageImpl<>(
                paginatedPlugins.stream().map(BotPluginDto::fromEntity).toList(),
                pageable,
                plugins.size()
        );

        return ResponseEntity.ok(pluginDtos);
    }

    // Bot-specific Command Management APIs
    @PostMapping("/{botId}/commands")
    @Operation(summary = "Create command for bot", description = "Creates a new command for a specific bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommandDto> createCommandForBot(
            @PathVariable Long botId,
            @Valid @RequestBody CreateCommandRequest request) {
        log.info("Creating command for bot: {}", botId);

        // Set botId in request
        CreateCommandRequest botCommandRequest = new CreateCommandRequest();
        botCommandRequest.setBotId(botId);
        botCommandRequest.setCommand(request.getCommand());
        botCommandRequest.setDescription(request.getDescription());
        botCommandRequest.setType(request.getType());
        botCommandRequest.setTrigger(request.getTrigger());
        botCommandRequest.setParameters(request.getParameters());
        botCommandRequest.setResponseTemplate(request.getResponseTemplate());
        botCommandRequest.setPluginName(request.getPluginName());
        botCommandRequest.setPriority(request.getPriority());

        Command command = commandManagementUseCase.createCommand(botCommandRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommandDto.fromEntity(command));
    }

    @PutMapping("/{botId}/commands/{commandId}")
    @Operation(summary = "Update command for bot", description = "Updates a command for a specific bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommandDto> updateCommandForBot(
            @PathVariable Long botId,
            @PathVariable Long commandId,
            @Valid @RequestBody UpdateCommandRequest request) {
        log.info("Updating command {} for bot: {}", commandId, botId);

        Command command = commandManagementUseCase.updateCommand(commandId, request);
        return ResponseEntity.ok(CommandDto.fromEntity(command));
    }

    @DeleteMapping("/{botId}/commands/{commandId}")
    @Operation(summary = "Delete command for bot", description = "Deletes a command from a specific bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCommandForBot(
            @PathVariable Long botId,
            @PathVariable Long commandId) {
        log.info("Deleting command {} for bot: {}", commandId, botId);

        commandManagementUseCase.deleteCommand(commandId);
        return ResponseEntity.noContent().build();
    }

    // Bot-specific Plugin Management APIs
    @PostMapping("/{botId}/plugins")
    @Operation(summary = "Create plugin for bot", description = "Creates a new plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPluginDto> createPluginForBot(
            @PathVariable Long botId,
            @Valid @RequestBody CreatePluginRequest request) {
        log.info("Creating plugin for bot: {}", botId);
        request.setBotId(botId);
        BotPlugin plugin = pluginManagementUseCase.createPlugin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BotPluginDto.fromEntity(plugin));
    }

    @PutMapping("/{botId}/plugins/{pluginId}")
    @Operation(summary = "Update plugin for bot", description = "Updates plugin source code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPluginDto> updatePluginForBot(
            @PathVariable Long botId,
            @PathVariable String pluginId,
            @Valid @RequestBody UpdatePluginSourceRequest request) {
        log.info("Updating plugin {} for bot: {}", pluginId, botId);

        BotPlugin plugin = pluginManagementUseCase.updatePluginSource(pluginId, request);
        return ResponseEntity.ok(BotPluginDto.fromEntity(plugin));
    }

    @DeleteMapping("/{botId}/plugins/{pluginId}")
    @Operation(summary = "Delete plugin for bot", description = "Deletes a plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePluginForBot(
            @PathVariable Long botId,
            @PathVariable String pluginId) {
        log.info("Deleting plugin {} for bot: {}", pluginId, botId);

        pluginManagementUseCase.deletePlugin(pluginId);
        return ResponseEntity.noContent().build();
    }
}
