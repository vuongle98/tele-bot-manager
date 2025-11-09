package com.vuog.telebotmanager.application.usecase;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.presentation.dto.query.PluginQuery;
import com.vuog.telebotmanager.presentation.dto.request.CreatePluginRequest;
import com.vuog.telebotmanager.presentation.dto.request.UpdatePluginSourceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Application use case for plugin management
 * Follows Clean Architecture by defining application layer contracts
 */
public interface PluginManagementUseCase {

    /**
     * Create a new plugin
     */
    BotPlugin createPlugin(CreatePluginRequest request);

    /**
     * Update plugin source code
     */
    BotPlugin updatePluginSource(String pluginId, UpdatePluginSourceRequest request);

    /**
     * Compile plugin
     */
    BotPlugin compilePlugin(String pluginId);

    /**
     * Load plugin
     */
    BotPlugin loadPlugin(String pluginId);

    /**
     * Unload plugin
     */
    BotPlugin unloadPlugin(String pluginId);

    /**
     * Activate plugin
     */
    BotPlugin activatePlugin(String pluginId);

    /**
     * Deactivate plugin
     */
    BotPlugin deactivatePlugin(String pluginId);

    /**
     * Delete plugin
     */
    void deletePlugin(String pluginId);

    /**
     * Get plugin by ID
     */
    Optional<BotPlugin> getPluginById(String pluginId);

    /**
     * Get plugin by name
     */
    Optional<BotPlugin> getPluginByName(String name);

    /**
     * Get all plugins with pagination
     */
    Page<BotPlugin> getAllPlugins(Pageable pageable);

    /**
     * Search plugins by query with pagination
     */
    Page<BotPlugin> findAll(PluginQuery query, Pageable pageable);

    /**
     * Get active plugins
     */
    List<BotPlugin> getActivePlugins();

    /**
     * Get executable plugins
     */
    List<BotPlugin> getExecutablePlugins();

    /**
     * Get plugins by type
     */
    List<BotPlugin> getPluginsByType(BotPlugin.PluginType type);

    /**
     * Get plugins by status
     */
    List<BotPlugin> getPluginsByStatus(BotPlugin.PluginStatus status);

    /**
     * Get plugins that need compilation
     */
    List<BotPlugin> getPluginsNeedingCompilation();

    /**
     * Get plugins by author
     */
    Page<BotPlugin> getPluginsByAuthor(String author, Pageable pageable);

    /**
     * Execute plugin
     */
    CommandResponse executePlugin(String pluginName, CommandRequest request);

    /**
     * Reload plugin
     */
    BotPlugin reloadPlugin(String pluginId);

    /**
     * Get plugin statistics
     */
    PluginStatistics getPluginStatistics(String pluginId);

    /**
     * Plugin statistics interface
     */
    interface PluginStatistics {
        String getPluginId();

        String getPluginName();

        long getTotalExecutions();

        long getSuccessfulExecutions();

        long getFailedExecutions();

        double getSuccessRate();

        long getAverageExecutionTime();

        String getLastExecution();

        boolean isLoaded();

        boolean isActive();
    }
}
