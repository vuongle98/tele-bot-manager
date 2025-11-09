package com.vuog.telebotmanager.infrastructure.service;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.repository.PluginRepository;
import com.vuog.telebotmanager.domain.service.PluginManager;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.infrastructure.config.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuong.dynamicmoduleloader.PluginRuntimeService;
import org.vuong.dynamicmoduleloader.core.Plugin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic plugin manager implementation using the Dynamic Module Loader
 * Provides runtime compilation and execution of custom Java plugins
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DynamicPluginManager implements PluginManager {

    private final PluginRepository pluginRepository;
    private final PluginRuntimeService pluginRuntimeService = new PluginRuntimeService();
    private final AppSettings appSettings;

    private final Map<String, Plugin> loadedPlugins = new ConcurrentHashMap<>();
    private final Map<String, PluginExecutionStats> pluginStats = new ConcurrentHashMap<>();

    @Override
    public BotPlugin compileAndLoadPlugin(String pluginId, String sourceCode, String className, String methodName) {
        log.info("Compiling and loading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginId));

        try {
            // Update plugin source code
            botPlugin.updateSourceCode(sourceCode);
            botPlugin.setClassName(className);
            botPlugin.setMethodName(methodName);

            // Compile plugin using Dynamic Module Loader
            Plugin compiledPlugin = pluginRuntimeService.compileAndRegister(className, sourceCode);
            loadedPlugins.put(botPlugin.getName(), compiledPlugin);

            // Update plugin status
            botPlugin.compile();
            botPlugin.load();
            botPlugin.activate();
            pluginRepository.save(botPlugin);

            // Initialize stats
            pluginStats.put(botPlugin.getName(), new PluginExecutionStatsImpl(botPlugin.getName()));

            log.info("Plugin compiled and loaded successfully: {}", pluginId);
            return botPlugin;

        } catch (Exception e) {
            log.error("Error compiling and loading plugin: {}", pluginId, e);
            botPlugin.markAsError();
            pluginRepository.save(botPlugin);
            throw new RuntimeException("Failed to compile and load plugin", e);
        }
    }

    @Override
    public BotPlugin loadPlugin(String pluginId) {
        log.info("Loading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginId));

        if (botPlugin.getStatus() != BotPlugin.PluginStatus.COMPILED) {
            throw new IllegalStateException("Plugin must be compiled before loading: " + pluginId);
        }

        try {
            // Load plugin using Dynamic Module Loader
            Plugin compiledPlugin = pluginRuntimeService.compileAndRegister(
                    botPlugin.getClassName(), botPlugin.getSourceCode());
            loadedPlugins.put(botPlugin.getName(), compiledPlugin);

            // Update plugin status
            botPlugin.load();
            botPlugin.activate();
            pluginRepository.save(botPlugin);

            // Initialize stats
            pluginStats.put(botPlugin.getName(), new PluginExecutionStatsImpl(botPlugin.getName()));

            log.info("Plugin loaded successfully: {}", pluginId);
            return botPlugin;

        } catch (Exception e) {
            log.error("Error loading plugin: {}", pluginId, e);
            botPlugin.markAsError();
            pluginRepository.save(botPlugin);
            throw new RuntimeException("Failed to load plugin", e);
        }
    }

    @Override
    public void unloadPlugin(String pluginId) {
        log.info("Unloading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginId));

        try {
            // Remove from loaded plugins
            loadedPlugins.remove(botPlugin.getName());
            pluginStats.remove(botPlugin.getName());

            // Remove from Dynamic Module Loader registry
            pluginRuntimeService.removePlugin(botPlugin.getName());

            // Update plugin status
            botPlugin.deactivate();
            pluginRepository.save(botPlugin);

            log.info("Plugin unloaded successfully: {}", pluginId);

        } catch (Exception e) {
            log.error("Error unloading plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to unload plugin", e);
        }
    }

    @Override
    public CommandResponse executePlugin(String pluginName, CommandRequest request) {
        log.info("Executing plugin: {}", pluginName);

        if (!isPluginLoaded(pluginName)) {
            return CommandResponse.error(request.getCommandId(), "Plugin not loaded: " + pluginName, "PLUGIN_NOT_LOADED");
        }

        try {
            // Get plugin stats
            PluginExecutionStats stats = pluginStats.computeIfAbsent(pluginName, PluginExecutionStatsImpl::new);

            // Execute plugin using reflection
            Plugin plugin = loadedPlugins.get(pluginName);
            Class<?> clazz = plugin.getPluginClass();
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // Find the method to execute
            Method method = findExecutionMethod(clazz, request);
            if (method == null) {
                throw new RuntimeException("No suitable execution method found in plugin: " + pluginName);
            }

            int timeoutSec = Math.max(1, appSettings.getPluginDefaults().getTimeoutSeconds());
            int maxRetries = Math.max(0, appSettings.getPluginDefaults().getRetryCount());
            long start = System.currentTimeMillis();

            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                try {
                    CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            return method.invoke(instance, request.getInputText(), request.getParameters());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Object result = future.get(timeoutSec, TimeUnit.SECONDS);
                    String responseText = result != null ? result.toString() : "Plugin executed successfully";

                    // Update stats
                    stats.recordExecution(true, System.currentTimeMillis() - start);

                    log.info("Plugin executed successfully: {} (attempt {} of {})", pluginName, attempt + 1, maxRetries + 1);
                    return CommandResponse.success(request.getCommandId(), responseText);
                } catch (java.util.concurrent.TimeoutException te) {
                    log.warn("Plugin execution timed out after {}s: {} (attempt {} of {})", timeoutSec, pluginName, attempt + 1, maxRetries + 1);
                    if (attempt == maxRetries) {
                        stats.recordExecution(false, System.currentTimeMillis() - start);
                        return CommandResponse.error(request.getCommandId(), "Plugin execution timed out", "PLUGIN_TIMEOUT");
                    }
                } catch (Exception e) {
                    log.warn("Plugin execution failed: {} (attempt {} of {}): {}", pluginName, attempt + 1, maxRetries + 1, e.getMessage());
                    if (attempt == maxRetries) {
                        stats.recordExecution(false, System.currentTimeMillis() - start);
                        return CommandResponse.error(request.getCommandId(), "Plugin execution failed: " + e.getMessage(), "PLUGIN_EXECUTION_ERROR");
                    }
                }
            }

            stats.recordExecution(false, System.currentTimeMillis() - start);
            return CommandResponse.error(request.getCommandId(), "Plugin execution failed", "PLUGIN_EXECUTION_ERROR");

        } catch (Exception e) {
            log.error("Error executing plugin: {}", pluginName, e);

            // Update stats
            PluginExecutionStats stats = pluginStats.get(pluginName);
            if (stats != null) {
                stats.recordExecution(false, System.currentTimeMillis());
            }

            return CommandResponse.error(request.getCommandId(), "Plugin execution failed: " + e.getMessage(), "PLUGIN_EXECUTION_ERROR");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotPlugin> getLoadedPlugins() {
        return pluginRepository.findExecutablePlugins();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BotPlugin> getPluginByName(String name) {
        return pluginRepository.findByName(name);
    }

    @Override
    public boolean isPluginLoaded(String pluginName) {
        return loadedPlugins.containsKey(pluginName) && pluginRuntimeService.containsPlugin(pluginName);
    }

    @Override
    public BotPlugin reloadPlugin(String pluginId) {
        log.info("Reloading plugin: {}", pluginId);

        BotPlugin botPlugin = pluginRepository.findById(pluginId)
                .orElseThrow(() -> new IllegalArgumentException("Plugin not found: " + pluginId));

        try {
            // Unload first
            unloadPlugin(pluginId);

            // Load again
            return loadPlugin(pluginId);

        } catch (Exception e) {
            log.error("Error reloading plugin: {}", pluginId, e);
            throw new RuntimeException("Failed to reload plugin", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PluginExecutionStats getPluginStats(String pluginName) {
        return pluginStats.getOrDefault(pluginName, new PluginExecutionStatsImpl(pluginName));
    }

    @Override
    public void clearAllPlugins() {
        log.info("Clearing all loaded plugins");

        for (String pluginName : loadedPlugins.keySet()) {
            try {
                unloadPlugin(pluginName);
            } catch (Exception e) {
                log.error("Error unloading plugin during clear: {}", pluginName, e);
            }
        }

        loadedPlugins.clear();
        pluginStats.clear();
        pluginRuntimeService.clearPlugins();

        log.info("All plugins cleared");
    }

    /**
     * Find the appropriate execution method in the plugin class
     */
    private Method findExecutionMethod(Class<?> clazz, CommandRequest request) {
        try {
            // Try to find a method that matches the expected signature
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("execute") ||
                        method.getName().equals("process") ||
                        method.getName().equals("handle")) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes.length == 2 &&
                            paramTypes[0] == String.class &&
                            paramTypes[1] == Map.class) {
                        return method;
                    }
                }
            }

            // If no specific method found, try the first public method
            for (Method method : methods) {
                if (method.getParameterCount() <= 2) {
                    return method;
                }
            }

        } catch (Exception e) {
            log.error("Error finding execution method", e);
        }

        return null;
    }

    // Plugin execution stats implementation
    private static class PluginExecutionStatsImpl implements PluginExecutionStats {
        private final String pluginName;
        private long executionCount = 0;
        private long successCount = 0;
        private long errorCount = 0;
        private long totalExecutionTime = 0;
        private long lastExecutionTime = 0;

        public PluginExecutionStatsImpl(String pluginName) {
            this.pluginName = pluginName;
        }

        public void recordExecution(boolean success, long executionTime) {
            executionCount++;
            if (success) {
                successCount++;
            } else {
                errorCount++;
            }
            totalExecutionTime += executionTime;
            lastExecutionTime = executionTime;
        }

        @Override
        public String getPluginName() {
            return pluginName;
        }

        @Override
        public long getExecutionCount() {
            return executionCount;
        }

        @Override
        public long getSuccessCount() {
            return successCount;
        }

        @Override
        public long getErrorCount() {
            return errorCount;
        }

        @Override
        public double getAverageExecutionTime() {
            return executionCount > 0 ? (double) totalExecutionTime / executionCount : 0.0;
        }

        @Override
        public long getLastExecutionTime() {
            return lastExecutionTime;
        }
    }
}
