package com.vuog.telebotmanager.domain.service;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;

import java.util.List;
import java.util.Optional;

/**
 * Domain service interface for plugin management
 * Follows Clean Architecture by defining service contracts in domain layer
 */
public interface PluginManager {

    /**
     * Compile and load a plugin from source code
     */
    BotPlugin compileAndLoadPlugin(String pluginId, String sourceCode, String className, String methodName);

    /**
     * Load an existing compiled plugin
     */
    BotPlugin loadPlugin(String pluginId);

    /**
     * Unload a plugin
     */
    void unloadPlugin(String pluginId);

    /**
     * Execute a plugin with given parameters
     */
    CommandResponse executePlugin(String pluginName, CommandRequest request);

    /**
     * Get all loaded plugins
     */
    List<BotPlugin> getLoadedPlugins();

    /**
     * Get plugin by name
     */
    Optional<BotPlugin> getPluginByName(String name);

    /**
     * Check if plugin is loaded and available
     */
    boolean isPluginLoaded(String pluginName);

    /**
     * Reload a plugin (unload and load again)
     */
    BotPlugin reloadPlugin(String pluginId);

    /**
     * Get plugin execution statistics
     */
    PluginExecutionStats getPluginStats(String pluginName);

    /**
     * Clear all loaded plugins
     */
    void clearAllPlugins();

    /**
     * Plugin execution statistics
     */
    interface PluginExecutionStats {
        String getPluginName();

        long getExecutionCount();

        long getSuccessCount();

        long getErrorCount();

        double getAverageExecutionTime();

        long getLastExecutionTime();

        void recordExecution(boolean b, long l);
    }
}
