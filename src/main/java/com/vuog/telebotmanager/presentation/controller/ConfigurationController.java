package com.vuog.telebotmanager.presentation.controller;

import com.vuog.telebotmanager.application.usecase.ConfigurationUseCase;
import com.vuog.telebotmanager.domain.entity.Configuration;
import com.vuog.telebotmanager.presentation.dto.request.SetConfigurationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for configuration management
 * Provides API endpoints for database-driven configuration
 */
@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configuration Management", description = "API for managing system configurations")
public class ConfigurationController {

    private final ConfigurationUseCase configurationService;

    @GetMapping("/{key}")
    @Operation(summary = "Get configuration value", description = "Retrieves a configuration value by key")
    public ResponseEntity<String> getConfigurationValue(@PathVariable String key) {
        log.info("Getting configuration value for key: {}", key);

        Optional<String> value = configurationService.getConfigurationValue(key);
        return value.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{key}/default")
    @Operation(summary = "Get configuration value with default", description = "Retrieves a configuration value with default fallback")
    public ResponseEntity<String> getConfigurationValueWithDefault(@PathVariable String key,
                                                                   @RequestParam String defaultValue) {
        log.info("Getting configuration value for key: {} with default: {}", key, defaultValue);

        String value = configurationService.getConfigurationValue(key, defaultValue);
        return ResponseEntity.ok(value);
    }

    @PutMapping("/{key}")
    @Operation(summary = "Set configuration value", description = "Sets a configuration value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setConfigurationValue(@PathVariable String key,
                                                      @RequestBody SetConfigurationRequest request) {
        log.info("Setting configuration value for key: {}", key);

        configurationService.setConfigurationValue(key, request.getValue());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bot/{botId}")
    @Operation(summary = "Get bot configurations", description = "Retrieves all configurations for a specific bot")
    public ResponseEntity<Map<String, String>> getBotConfigurations(@PathVariable Long botId) {
        log.info("Getting configurations for bot: {}", botId);

        Map<String, String> configurations = configurationService.getBotConfigurations(botId);
        return ResponseEntity.ok(configurations);
    }

    @PutMapping("/bot/{botId}/{key}")
    @Operation(summary = "Set bot configuration", description = "Sets a configuration value for a specific bot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setBotConfiguration(@PathVariable Long botId,
                                                    @PathVariable String key,
                                                    @RequestBody SetConfigurationRequest request) {
        log.info("Setting configuration for bot: {} with key: {}", botId, key);

        configurationService.setBotConfiguration(botId, key, request.getValue());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ai")
    @Operation(summary = "Get AI configurations", description = "Retrieves all AI-related configurations")
    public ResponseEntity<Map<String, String>> getAiConfigurations() {
        log.info("Getting AI configurations");

        Map<String, String> configurations = configurationService.getAiConfigurations();
        return ResponseEntity.ok(configurations);
    }

    @PutMapping("/ai/{key}")
    @Operation(summary = "Set AI configuration", description = "Sets an AI configuration value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setAiConfiguration(@PathVariable String key,
                                                   @RequestBody SetConfigurationRequest request) {
        log.info("Setting AI configuration for key: {}", key);

        configurationService.setAiConfiguration(key, request.getValue());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/plugin/{pluginName}")
    @Operation(summary = "Get plugin configurations", description = "Retrieves all configurations for a specific plugin")
    public ResponseEntity<Map<String, String>> getPluginConfigurations(@PathVariable String pluginName) {
        log.info("Getting configurations for plugin: {}", pluginName);

        Map<String, String> configurations = configurationService.getPluginConfigurations(pluginName);
        return ResponseEntity.ok(configurations);
    }

    @PutMapping("/plugin/{pluginName}/{key}")
    @Operation(summary = "Set plugin configuration", description = "Sets a configuration value for a specific plugin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setPluginConfiguration(@PathVariable String pluginName,
                                                       @PathVariable String key,
                                                       @RequestBody SetConfigurationRequest request) {
        log.info("Setting configuration for plugin: {} with key: {}", pluginName, key);

        configurationService.setPluginConfiguration(pluginName, key, request.getValue());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/global")
    @Operation(summary = "Get global configurations", description = "Retrieves all global configurations")
    public ResponseEntity<Map<String, String>> getGlobalConfigurations() {
        log.info("Getting global configurations");

        Map<String, String> configurations = configurationService.getGlobalConfigurations();
        return ResponseEntity.ok(configurations);
    }

    @GetMapping("/scope/{scope}")
    @Operation(summary = "Get configurations by scope", description = "Retrieves configurations by scope")
    public ResponseEntity<List<Configuration>> getConfigurationsByScope(@PathVariable Configuration.ConfigurationScope scope) {
        log.info("Getting configurations by scope: {}", scope);

        List<Configuration> configurations = configurationService.getConfigurationsByScope(scope);
        return ResponseEntity.ok(configurations);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get configurations by type", description = "Retrieves configurations by type")
    public ResponseEntity<List<Configuration>> getConfigurationsByType(@PathVariable Configuration.ConfigurationType type) {
        log.info("Getting configurations by type: {}", type);

        List<Configuration> configurations = configurationService.getConfigurationsByType(type);
        return ResponseEntity.ok(configurations);
    }

    @PostMapping("/initialize")
    @Operation(summary = "Initialize default configurations", description = "Initializes default system configurations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> initializeDefaultConfigurations() {
        log.info("Initializing default configurations");

        configurationService.initializeDefaultConfigurations();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "Delete configuration", description = "Deletes a configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable String key) {
        log.info("Deleting configuration: {}", key);

        configurationService.deleteConfiguration(key);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{key}/activate")
    @Operation(summary = "Activate configuration", description = "Activates a configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateConfiguration(@PathVariable String key) {
        log.info("Activating configuration: {}", key);

        configurationService.activateConfiguration(key);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{key}/deactivate")
    @Operation(summary = "Deactivate configuration", description = "Deactivates a configuration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateConfiguration(@PathVariable String key) {
        log.info("Deactivating configuration: {}", key);

        configurationService.deactivateConfiguration(key);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get configuration statistics", description = "Retrieves configuration statistics")
    public ResponseEntity<ConfigurationUseCase.ConfigurationServiceStats> getConfigurationStats() {
        log.info("Getting configuration statistics");

        ConfigurationUseCase.ConfigurationServiceStats stats = configurationService.getConfigurationStats();
        return ResponseEntity.ok(stats);
    }
}
