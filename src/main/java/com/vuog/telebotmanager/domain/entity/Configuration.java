package com.vuog.telebotmanager.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Core domain entity representing system configurations
 * Supports dynamic configuration management with versioning
 */
@Entity
@Table(name = "configurations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Configuration {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "key_name", nullable = false, unique = true)
    private String keyName;

    @Column(name = "value", columnDefinition = "TEXT", nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConfigurationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private ConfigurationScope scope;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "metadata", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "version", nullable = false)
    private String version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    private String updatedBy;

    /**
     * Domain method to activate the configuration
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Domain method to deactivate the configuration
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Domain method to update configuration value with version increment
     */
    public void updateValue(String newValue, String updatedBy) {
        this.value = newValue;
        this.updatedBy = updatedBy;
        incrementVersion();
    }

    /**
     * Domain method to increment version
     */
    private void incrementVersion() {
        try {
            String[] versionParts = version.split("\\.");
            int major = Integer.parseInt(versionParts[0]);
            int minor = Integer.parseInt(versionParts[1]);
            int patch = Integer.parseInt(versionParts[2]);

            patch++;
            if (patch > 99) {
                patch = 0;
                minor++;
                if (minor > 99) {
                    minor = 0;
                    major++;
                }
            }

            this.version = major + "." + minor + "." + patch;
        } catch (Exception e) {
            // If version parsing fails, reset to 1.0.0
            this.version = "1.0.0";
        }
    }

    /**
     * Check if configuration is valid for the given scope
     */
    public boolean isValidForScope(ConfigurationScope targetScope) {
        return scope == ConfigurationScope.GLOBAL || scope == targetScope;
    }

    public enum ConfigurationType {
        STRING, INTEGER, BOOLEAN, JSON, URL, EMAIL, TOKEN, PASSWORD, FILE_PATH
    }

    public enum ConfigurationScope {
        GLOBAL, BOT_SPECIFIC, USER_SPECIFIC, PLUGIN_SPECIFIC, AI_SPECIFIC
    }
}
