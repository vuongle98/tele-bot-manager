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
 * Core domain entity representing a Bot Command
 * Supports dynamic command execution with various trigger types
 */
@Entity
@Table(name = "bot_commands")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id", nullable = true)
    private Bot bot;

    @Column(name = "command", nullable = false)
    private String command;

    @Column(name = "response_template", length = 1000)
    private String responseTemplate;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CommandType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger", nullable = false)
    private TriggerType trigger;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private Category category;

    @Column(name = "parameters", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode parameters;

    @Column(name = "plugin_name")
    private String pluginName;

    @Column(name = "additional_config", columnDefinition = "TEXT")
    private String additionalConfig;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds;

    @Column(name = "retry_count")
    private Integer retryCount;

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

    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommandExecution> executions;

    /**
     * Domain method to enable the command
     */
    public void enable() {
        this.isEnabled = true;
    }

    /**
     * Domain method to disable the command
     */
    public void disable() {
        this.isEnabled = false;
    }

    /**
     * Check if command is executable
     */
    public boolean isExecutable() {
        return isEnabled && bot != null && bot.isOperational();
    }

    /**
     * Check if command requires plugin execution
     */
    public boolean requiresPlugin() {
        return type == CommandType.PLUGIN && pluginName != null && !pluginName.trim().isEmpty();
    }

    /**
     * Check if command is AI-powered
     */
    public boolean isAiPowered() {
        return type == CommandType.AI_TASK || type == CommandType.AI_ANSWER ||
                type == CommandType.SUMMARY || type == CommandType.GENERATION ||
                type == CommandType.ANALYSIS;
    }

    public enum CommandType {
        SCHEDULE, REMINDER, AI_TASK, SUMMARY, PLUGIN, CUSTOM, AI_ANSWER, GENERATION, ANALYSIS
    }

    public enum TriggerType {
        MANUAL, KEYWORD, CRON, EVENT_BASED, WEBHOOK, SCHEDULED
    }

    public enum Category {
        PLUGIN, AI, DEFAULT
    }
}
