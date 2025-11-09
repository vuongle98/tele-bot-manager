package com.vuog.telebotmanager.presentation.controller;

import com.vuog.telebotmanager.application.usecase.PluginManagementUseCase;
import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.query.PluginQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreatePluginRequest;
import com.vuog.telebotmanager.presentation.dto.request.ExecutePluginRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdatePluginSourceRequest;
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
 * REST controller for plugin management operations
 * Provides API endpoints for plugin CRUD operations and execution
 */
@RestController
@RequestMapping("/api/v1/plugins")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Plugin Management", description = "API for managing dynamic plugins")
public class PluginController {

    private final PluginManagementUseCase pluginManagementUseCase;

    @PostMapping
    @Operation(summary = "Create a new plugin", description = "Creates a new plugin with source code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> createPlugin(@Valid @RequestBody CreatePluginRequest request) {
        log.info("Creating plugin: {}", request.getName());

        BotPlugin botPlugin = pluginManagementUseCase.createPlugin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(botPlugin);
    }

    @GetMapping("/{pluginId}")
    @Operation(summary = "Get plugin by ID", description = "Retrieves a plugin by its unique identifier")
    public ResponseEntity<BotPlugin> getPluginById(@PathVariable String pluginId) {
        log.info("Getting plugin by ID: {}", pluginId);

        Optional<BotPlugin> plugin = pluginManagementUseCase.getPluginById(pluginId);
        return plugin.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all plugins", description = "Retrieves all plugins with pagination and filtering")
    public ResponseEntity<Page<BotPlugin>> getAllPlugins(PluginQuery query, Pageable pageable) {
        log.info("Getting all plugins with pagination and query: {}", query);

        Page<BotPlugin> plugins = pluginManagementUseCase.findAll(query, pageable);
        return ResponseEntity.ok(plugins);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active plugins", description = "Retrieves all active plugins")
    public ResponseEntity<List<BotPlugin>> getActivePlugins() {
        log.info("Getting active plugins");

        List<BotPlugin> botPlugins = pluginManagementUseCase.getActivePlugins();
        return ResponseEntity.ok(botPlugins);
    }

    @GetMapping("/executable")
    @Operation(summary = "Get executable plugins", description = "Retrieves all executable plugins")
    public ResponseEntity<List<BotPlugin>> getExecutablePlugins() {
        log.info("Getting executable plugins");

        List<BotPlugin> botPlugins = pluginManagementUseCase.getExecutablePlugins();
        return ResponseEntity.ok(botPlugins);
    }

    @GetMapping("/needing-compilation")
    @Operation(summary = "Get plugins needing compilation", description = "Retrieves plugins that need compilation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BotPlugin>> getPluginsNeedingCompilation() {
        log.info("Getting plugins needing compilation");

        List<BotPlugin> botPlugins = pluginManagementUseCase.getPluginsNeedingCompilation();
        return ResponseEntity.ok(botPlugins);
    }

    @PutMapping("/{pluginId}/source")
    @Operation(summary = "Update plugin source code", description = "Updates plugin source code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> updatePluginSource(@PathVariable String pluginId, @Valid @RequestBody UpdatePluginSourceRequest request) {
        log.info("Updating source code for plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.updatePluginSource(pluginId, request);
        return ResponseEntity.ok(botPlugin);
    }

    @PostMapping("/{pluginId}/compile")
    @Operation(summary = "Compile plugin", description = "Compiles plugin source code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> compilePlugin(@PathVariable String pluginId) {
        log.info("Compiling plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.compilePlugin(pluginId);
        return ResponseEntity.ok(botPlugin);
    }

    @PostMapping("/{pluginId}/load")
    @Operation(summary = "Load plugin", description = "Loads a compiled plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> loadPlugin(@PathVariable String pluginId) {
        log.info("Loading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.loadPlugin(pluginId);
        return ResponseEntity.ok(botPlugin);
    }

    @PostMapping("/{pluginId}/unload")
    @Operation(summary = "Unload plugin", description = "Unloads a plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> unloadPlugin(@PathVariable String pluginId) {
        log.info("Unloading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.unloadPlugin(pluginId);
        return ResponseEntity.ok(botPlugin);
    }

    @PostMapping("/{pluginId}/activate")
    @Operation(summary = "Activate plugin", description = "Activates a plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> activatePlugin(@PathVariable String pluginId) {
        log.info("Activating plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.activatePlugin(pluginId);
        return ResponseEntity.ok(botPlugin);
    }

    @PostMapping("/{pluginId}/deactivate")
    @Operation(summary = "Deactivate plugin", description = "Deactivates a plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> deactivatePlugin(@PathVariable String pluginId) {
        log.info("Deactivating plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.deactivatePlugin(pluginId);
        return ResponseEntity.ok(botPlugin);
    }

    @PostMapping("/{pluginId}/reload")
    @Operation(summary = "Reload plugin", description = "Reloads a plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BotPlugin> reloadPlugin(@PathVariable String pluginId) {
        log.info("Reloading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginManagementUseCase.reloadPlugin(pluginId);
        return ResponseEntity.ok(botPlugin);
    }

    @DeleteMapping("/{pluginId}")
    @Operation(summary = "Delete plugin", description = "Deletes a plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlugin(@PathVariable String pluginId) {
        log.info("Deleting plugin: {}", pluginId);

        pluginManagementUseCase.deletePlugin(pluginId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/execute")
    @Operation(summary = "Execute plugin", description = "Executes a plugin")
    public ResponseEntity<CommandResponse> executePlugin(@Valid @RequestBody ExecutePluginRequest request) {
        log.info("Executing plugin: {}", request.getPluginName());

        CommandRequest commandRequest = CommandRequest.create(
                request.getCommandId(),
                request.getBotId(),
                request.getUserId(),
                request.getChatId(),
                request.getCommand(),
                request.getInputText()
        );

        CommandResponse response = pluginManagementUseCase.executePlugin(request.getPluginName(), commandRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pluginId}/statistics")
    @Operation(summary = "Get plugin statistics", description = "Retrieves statistics for a plugin")
    public ResponseEntity<PluginManagementUseCase.PluginStatistics> getPluginStatistics(@PathVariable String pluginId) {
        log.info("Getting statistics for plugin: {}", pluginId);

        PluginManagementUseCase.PluginStatistics stats = pluginManagementUseCase.getPluginStatistics(pluginId);
        return ResponseEntity.ok(stats);
    }
}
