package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Plugin entity
 * Follows Clean Architecture by defining repository contracts in domain layer
 */
@Repository
public interface PluginRepository extends JpaRepository<BotPlugin, String>, BaseQueryRepository<BotPlugin, String> {

    /**
     * Find plugin by name
     */
    Optional<BotPlugin> findByName(String name);

    /**
     * Find active plugins
     */
    List<BotPlugin> findByIsActiveTrue();

    /**
     * Find plugins by status
     */
    List<BotPlugin> findByStatus(BotPlugin.PluginStatus status);

    /**
     * Find plugins by type
     */
    List<BotPlugin> findByType(BotPlugin.PluginType type);

    /**
     * Find active plugins by status
     */
    List<BotPlugin> findByStatusAndIsActive(BotPlugin.PluginStatus status, Boolean isActive);

    /**
     * Find active plugins by type
     */
    List<BotPlugin> findByTypeAndIsActive(BotPlugin.PluginType type, Boolean isActive);

    /**
     * Find plugins by author
     */
    List<BotPlugin> findByAuthor(String author);

    /**
     * Find plugins created by user
     */
    Page<BotPlugin> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * Find plugins with pagination
     */
    Page<BotPlugin> findAll(Pageable pageable);

    /**
     * Check if plugin name exists
     */
    boolean existsByName(String name);

    /**
     * Find executable plugins (active and status = ACTIVE)
     */
    @Query("SELECT p FROM BotPlugin p WHERE p.isActive = true AND p.status = 'ACTIVE'")
    List<BotPlugin> findExecutablePlugins();

    /**
     * Find plugins that need compilation
     */
    @Query("SELECT p FROM BotPlugin p WHERE p.status = 'DRAFT' AND p.sourceCode IS NOT NULL")
    List<BotPlugin> findPluginsNeedingCompilation();

    /**
     * Find plugins by type and status
     */
    @Query("SELECT p FROM BotPlugin p WHERE p.type = :type AND p.status = :status AND p.isActive = true")
    List<BotPlugin> findByTypeAndStatus(@Param("type") BotPlugin.PluginType type,
                                        @Param("status") BotPlugin.PluginStatus status);

    /**
     * Find command handler plugins
     */
    @Query("SELECT p FROM BotPlugin p WHERE p.type = 'COMMAND_HANDLER' AND p.isActive = true")
    List<BotPlugin> findCommandHandlerPlugins();

    /**
     * Find AI processor plugins
     */
    @Query("SELECT p FROM BotPlugin p WHERE p.type = 'AI_PROCESSOR' AND p.isActive = true")
    List<BotPlugin> findAiProcessorPlugins();

    /**
     * Find plugins by version
     */
    List<BotPlugin> findByVersion(String version);

    /**
     * Find latest plugins (highest version)
     */
    @Query("SELECT p FROM BotPlugin p WHERE p.name = :name ORDER BY p.version DESC")
    List<BotPlugin> findLatestByName(@Param("name") String name);

    /**
     * Count plugins by status
     */
    @Query("SELECT COUNT(p) FROM BotPlugin p WHERE p.status = :status")
    long countByStatus(@Param("status") BotPlugin.PluginStatus status);

    /**
     * Count active plugins by type
     */
    @Query("SELECT COUNT(p) FROM BotPlugin p WHERE p.type = :type AND p.isActive = true")
    long countActiveByType(@Param("type") BotPlugin.PluginType type);

    /**
     * Find plugins by author with pagination
     */
    Page<BotPlugin> findByAuthor(String author, Pageable pageable);

    /**
     * Find plugins by type with pagination
     */
    Page<BotPlugin> findByType(BotPlugin.PluginType type, Pageable pageable);

    /**
     * Find plugins by status with pagination
     */
    Page<BotPlugin> findByStatus(BotPlugin.PluginStatus status, Pageable pageable);
}
