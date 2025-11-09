package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.application.specification.PluginSpecification;
import com.vuog.telebotmanager.application.usecase.PluginManagementUseCase;
import com.vuog.telebotmanager.domain.entity.Bot;
import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.repository.BotRepository;
import com.vuog.telebotmanager.domain.repository.PluginRepository;
import com.vuog.telebotmanager.domain.service.PluginManager;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.query.PluginQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreatePluginRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdatePluginSourceRequest;
import com.vuog.telebotmanager.application.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing plugin management use cases
 * Follows Clean Architecture by implementing application layer contracts
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PluginManagementService implements PluginManagementUseCase {

    private final PluginRepository pluginRepository;
    private final BotRepository botRepository;
    private final PluginManager pluginManager;
    private final PermissionService permissionService;

    @Override
    public BotPlugin createPlugin(CreatePluginRequest request) {
        log.info("Creating plugin: {}", request.getName());

        // if (!permissionService.canCreatePlugin(null)) {
        // throw new SecurityException("User is not allowed to create plugins");
        // }

        BotPlugin plugin = BotPlugin.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .sourceCode(request.getSourceCode())
                .type(request.getType())
                .status(BotPlugin.PluginStatus.DRAFT)
                .version("1.0.0")
                .className(request.getClassName())
                .methodName(request.getMethodName())
                .isActive(false)
                .author(request.getAuthor())
                .build();

        // Set bot if botId is provided
        if (request.getBotId() != null) {
            Bot bot = botRepository.findById(request.getBotId())
                    .orElseThrow(() -> new IllegalArgumentException("Bot not found with ID: " + request.getBotId()));
            plugin.setBot(bot);
        }

        BotPlugin savedPlugin = pluginRepository.save(plugin);

        log.info("Plugin created successfully with ID: {}", savedPlugin.getId());
        return savedPlugin;
    }

    @Override
    public BotPlugin updatePluginSource(String pluginId, UpdatePluginSourceRequest request) {
        log.info("Updating source code for plugin: {}", pluginId);

        if (!permissionService.canCreatePlugin(null)) {
            throw new SecurityException("User is not allowed to update plugins");
        }

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        plugin.updateSourceCode(request.getSourceCode());
        BotPlugin updatedPlugin = pluginRepository.save(plugin);

        log.info("Plugin source code updated successfully with ID: {}", updatedPlugin.getId());
        return updatedPlugin;
    }

    @Override
    public BotPlugin compilePlugin(String pluginId) {
        log.info("Compiling plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        try {
            plugin.compile();
            BotPlugin compiledPlugin = pluginRepository.save(plugin);

            log.info("Plugin compiled successfully with ID: {}", compiledPlugin.getId());
            return compiledPlugin;

        } catch (Exception e) {
            log.error("Error compiling plugin: {}", pluginId, e);
            plugin.markAsError();
            pluginRepository.save(plugin);
            throw new RuntimeException("Failed to compile plugin", e);
        }
    }

    @Override
    public BotPlugin loadPlugin(String pluginId) {
        log.info("Loading plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        try {
            BotPlugin loadedPlugin = pluginManager.loadPlugin(pluginId);
            BotPlugin savedPlugin = pluginRepository.save(loadedPlugin);

            log.info("Plugin loaded successfully with ID: {}", savedPlugin.getId());
            return savedPlugin;

        } catch (Exception e) {
            log.error("Error loading plugin: {}", pluginId, e);
            plugin.markAsError();
            pluginRepository.save(plugin);
            throw new RuntimeException("Failed to load plugin", e);
        }
    }

    @Override
    public BotPlugin unloadPlugin(String pluginId) {
        log.info("Unloading plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        try {
            pluginManager.unloadPlugin(pluginId);
            plugin.deactivate();
            BotPlugin unloadedPlugin = pluginRepository.save(plugin);

            log.info("Plugin unloaded successfully with ID: {}", unloadedPlugin.getId());
            return unloadedPlugin;

        } catch (Exception e) {
            log.error("Error unloading plugin: {}", pluginId, e);
            plugin.markAsError();
            pluginRepository.save(plugin);
            throw new RuntimeException("Failed to unload plugin", e);
        }
    }

    @Override
    public BotPlugin activatePlugin(String pluginId) {
        log.info("Activating plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        plugin.activate();
        BotPlugin activatedPlugin = pluginRepository.save(plugin);

        log.info("Plugin activated successfully with ID: {}", activatedPlugin.getId());
        return activatedPlugin;
    }

    @Override
    public BotPlugin deactivatePlugin(String pluginId) {
        log.info("Deactivating plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        plugin.deactivate();
        BotPlugin deactivatedPlugin = pluginRepository.save(plugin);

        log.info("Plugin deactivated successfully with ID: {}", deactivatedPlugin.getId());
        return deactivatedPlugin;
    }

    @Override
    public void deletePlugin(String pluginId) {
        log.info("Deleting plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        try {
            pluginManager.unloadPlugin(pluginId);
        } catch (Exception e) {
            log.warn("Error unloading plugin during deletion: {}", pluginId, e);
        }

        pluginRepository.delete(plugin);

        log.info("Plugin deleted successfully with ID: {}", pluginId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BotPlugin> getPluginById(String pluginId) {
        return pluginRepository.findById(pluginId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BotPlugin> getPluginByName(String name) {
        return pluginRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BotPlugin> getAllPlugins(Pageable pageable) {
        return pluginRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BotPlugin> findAll(PluginQuery query, Pageable pageable) {
        Specification<BotPlugin> pluginSpecification = PluginSpecification.withFilter(query);
        return pluginRepository.findAll(pluginSpecification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotPlugin> getActivePlugins() {
        return pluginRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotPlugin> getExecutablePlugins() {
        return pluginRepository.findExecutablePlugins();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotPlugin> getPluginsByType(BotPlugin.PluginType type) {
        return pluginRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotPlugin> getPluginsByStatus(BotPlugin.PluginStatus status) {
        return pluginRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotPlugin> getPluginsNeedingCompilation() {
        return pluginRepository.findPluginsNeedingCompilation();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BotPlugin> getPluginsByAuthor(String author, Pageable pageable) {
        return pluginRepository.findByAuthor(author, pageable);
    }

    @Override
    public CommandResponse executePlugin(String pluginName, CommandRequest request) {
        log.info("Executing plugin: {}", pluginName);

        try {
            CommandResponse response = pluginManager.executePlugin(pluginName, request);
            log.info("Plugin executed successfully: {}", pluginName);
            return response;
        } catch (Exception e) {
            log.error("Error executing plugin: {}", pluginName, e);
            return CommandResponse.error(request.getCommandId(), "Plugin execution failed: " + e.getMessage(),
                    "PLUGIN_EXECUTION_ERROR");
        }
    }

    @Override
    public BotPlugin reloadPlugin(String pluginId) {
        log.info("Reloading plugin: {}", pluginId);

        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        try {
            BotPlugin reloadedPlugin = pluginManager.reloadPlugin(pluginId);
            BotPlugin savedPlugin = pluginRepository.save(reloadedPlugin);

            log.info("Plugin reloaded successfully with ID: {}", savedPlugin.getId());
            return savedPlugin;

        } catch (Exception e) {
            log.error("Error reloading plugin: {}", pluginId, e);
            plugin.markAsError();
            pluginRepository.save(plugin);
            throw new RuntimeException("Failed to reload plugin", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PluginStatistics getPluginStatistics(String pluginId) {
        BotPlugin plugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found with ID: " + pluginId));

        PluginManager.PluginExecutionStats stats = pluginManager.getPluginStats(plugin.getName());

        return new PluginStatistics() {
            @Override
            public String getPluginId() {
                return plugin.getId();
            }

            @Override
            public String getPluginName() {
                return plugin.getName();
            }

            @Override
            public long getTotalExecutions() {
                return stats != null ? stats.getExecutionCount() : 0;
            }

            @Override
            public long getSuccessfulExecutions() {
                return stats != null ? stats.getSuccessCount() : 0;
            }

            @Override
            public long getFailedExecutions() {
                return stats != null ? stats.getErrorCount() : 0;
            }

            @Override
            public double getSuccessRate() {
                if (stats == null || stats.getExecutionCount() == 0) {
                    return 0.0;
                }
                return (double) stats.getSuccessCount() / stats.getExecutionCount() * 100;
            }

            @Override
            public long getAverageExecutionTime() {
                return stats != null ? (long) stats.getAverageExecutionTime() : 0;
            }

            @Override
            public String getLastExecution() {
                return plugin.getUpdatedAt().toString();
            }

            @Override
            public boolean isLoaded() {
                return pluginManager.isPluginLoaded(plugin.getName());
            }

            @Override
            public boolean isActive() {
                return plugin.getIsActive();
            }
        };
    }
}
