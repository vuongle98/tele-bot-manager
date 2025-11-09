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
import java.util.List;

/**
 * Core domain entity representing a dynamic plugin
 * Supports runtime compilation and execution of custom Java code
 */
@Entity
@Table(name = "plugins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class BotPlugin {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "source_code", columnDefinition = "TEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PluginType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PluginStatus status;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "metadata", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "author")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id", nullable = true)
    private Bot bot;

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

    @OneToMany(mappedBy = "botPlugin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PluginAlias> aliases;

    /**
     * Domain method to compile the plugin
     */
    public void compile() {
        this.status = PluginStatus.COMPILED;
    }

    /**
     * Domain method to load the plugin
     */
    public void load() {
        if (status == PluginStatus.COMPILED) {
            this.status = PluginStatus.LOADED;
        }
    }

    /**
     * Domain method to activate the plugin
     */
    public void activate() {
        if (status == PluginStatus.LOADED) {
            this.status = PluginStatus.ACTIVE;
            this.isActive = true;
        }
    }

    /**
     * Domain method to deactivate the plugin
     */
    public void deactivate() {
        this.status = PluginStatus.DISABLED;
        this.isActive = false;
    }

    /**
     * Domain method to mark plugin as error
     */
    public void markAsError() {
        this.status = PluginStatus.ERROR;
        this.isActive = false;
    }

    /**
     * Domain method to update plugin source code
     */
    public void updateSourceCode(String newSourceCode) {
        this.sourceCode = newSourceCode;
        this.status = PluginStatus.DRAFT;
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
     * Check if plugin is executable
     */
    public boolean isExecutable() {
        return isActive && status == PluginStatus.ACTIVE &&
                sourceCode != null && !sourceCode.trim().isEmpty();
    }

    /**
     * Check if plugin needs compilation
     */
    public boolean needsCompilation() {
        return status == PluginStatus.DRAFT && sourceCode != null && !sourceCode.trim().isEmpty();
    }

    public enum PluginType {
        COMMAND_HANDLER, AI_PROCESSOR, DATA_TRANSFORMER, SCHEDULER, WEBHOOK_HANDLER, CUSTOM
    }

    public enum PluginStatus {
        DRAFT, COMPILED, LOADED, ACTIVE, ERROR, DISABLED, DEPRECATED
    }
}