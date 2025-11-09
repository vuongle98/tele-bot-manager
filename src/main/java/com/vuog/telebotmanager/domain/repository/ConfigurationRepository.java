package com.vuog.telebotmanager.domain.repository;

import com.vuog.telebotmanager.domain.entity.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Configuration entity
 * Follows Clean Architecture by defining repository contracts in domain layer
 */
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, String> {

    /**
     * Find configuration by key name
     */
    Optional<Configuration> findByKeyName(String keyName);

    /**
     * Find active configurations
     */
    List<Configuration> findByIsActiveTrue();

    /**
     * Find configurations by scope
     */
    List<Configuration> findByScope(Configuration.ConfigurationScope scope);

    /**
     * Find configurations by type
     */
    List<Configuration> findByType(Configuration.ConfigurationType type);

    /**
     * Find active configurations by scope
     */
    List<Configuration> findByScopeAndIsActive(Configuration.ConfigurationScope scope, Boolean isActive);

    /**
     * Find active configurations by type
     */
    List<Configuration> findByTypeAndIsActive(Configuration.ConfigurationType type, Boolean isActive);

    /**
     * Find configurations by scope and type
     */
    List<Configuration> findByScopeAndType(Configuration.ConfigurationScope scope,
                                           Configuration.ConfigurationType type);

    /**
     * Find active configurations by scope and type
     */
    List<Configuration> findByScopeAndTypeAndIsActive(Configuration.ConfigurationScope scope,
                                                      Configuration.ConfigurationType type,
                                                      Boolean isActive);

    /**
     * Find configurations created by user
     */
    Page<Configuration> findByCreatedBy(String createdBy, Pageable pageable);

    /**
     * Find configurations with pagination
     */
    Page<Configuration> findAll(Pageable pageable);

    /**
     * Check if configuration key exists
     */
    boolean existsByKeyName(String keyName);

    /**
     * Find configurations by key name pattern
     */
    @Query("SELECT c FROM Configuration c WHERE c.keyName LIKE :pattern AND c.isActive = true")
    List<Configuration> findByKeyNamePattern(@Param("pattern") String pattern);

    /**
     * Find AI-related configurations
     */
    @Query("SELECT c FROM Configuration c WHERE c.scope = 'AI_SPECIFIC' AND c.isActive = true")
    List<Configuration> findAiConfigurations();

    /**
     * Find bot-specific configurations
     */
    @Query("SELECT c FROM Configuration c WHERE c.scope = 'BOT_SPECIFIC' AND c.isActive = true")
    List<Configuration> findBotConfigurations();

    /**
     * Find plugin-specific configurations
     */
    @Query("SELECT c FROM Configuration c WHERE c.scope = 'PLUGIN_SPECIFIC' AND c.isActive = true")
    List<Configuration> findPluginConfigurations();

    /**
     * Find global configurations
     */
    @Query("SELECT c FROM Configuration c WHERE c.scope = 'GLOBAL' AND c.isActive = true")
    List<Configuration> findGlobalConfigurations();

    /**
     * Count configurations by scope
     */
    @Query("SELECT COUNT(c) FROM Configuration c WHERE c.scope = :scope")
    long countByScope(@Param("scope") Configuration.ConfigurationScope scope);

    /**
     * Count active configurations by scope
     */
    @Query("SELECT COUNT(c) FROM Configuration c WHERE c.scope = :scope AND c.isActive = true")
    long countActiveByScope(@Param("scope") Configuration.ConfigurationScope scope);

    /**
     * Find configurations by version
     */
    List<Configuration> findByVersion(String version);

    /**
     * Find latest configurations (highest version)
     */
    @Query("SELECT c FROM Configuration c WHERE c.keyName = :keyName ORDER BY c.version DESC")
    List<Configuration> findLatestByKeyName(@Param("keyName") String keyName);
}
