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
 * Core domain entity representing a Telegram Bot
 * Follows Clean Architecture principles with clear domain boundaries
 */
@Entity
@Table(name = "telegram_bots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bot_token", nullable = false, unique = true)
    private String botToken;

    @Column(name = "bot_username", nullable = false, unique = true)
    private String botUsername;

    @Column(name = "bot_name")
    private String botName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BotStatus status;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "description")
    private String description;

    @Column(name = "metadata", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

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

    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Command> commands;

    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BotHistory> history;

    /**
     * Domain method to activate the bot
     */
    public void activate() {
        this.status = BotStatus.ACTIVE;
        this.isActive = true;
    }

    /**
     * Domain method to deactivate the bot
     */
    public void deactivate() {
        this.status = BotStatus.INACTIVE;
        this.isActive = false;
    }

    /**
     * Domain method to suspended the bot (used for graceful shutdown)
     */
    public void suspended() {
        this.status = BotStatus.SUSPENDED;
        this.isActive = false;
    }

    /**
     * Domain method to mark bot as error state
     */
    public void markAsError() {
        this.status = BotStatus.ERROR;
        this.isActive = false;
    }

    /**
     * Check if bot is operational
     */
    public boolean isOperational() {
        return isActive && status == BotStatus.ACTIVE;
    }

    public enum BotStatus {
        ACTIVE, INACTIVE, ERROR, MAINTENANCE, SUSPENDED
    }
}
