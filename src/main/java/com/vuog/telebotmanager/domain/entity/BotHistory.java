package com.vuog.telebotmanager.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Domain entity representing bot status change history
 * Provides audit trail for bot lifecycle management
 */
@Entity
@Table(name = "bot_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class BotHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private Bot.BotStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private Bot.BotStatus newStatus;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "error_details", length = 2000)
    private String errorDetails;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "metadata", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    /**
     * Domain method to create a status change record
     */
    public static BotHistory createStatusChange(Bot bot, Bot.BotStatus previousStatus,
                                                Bot.BotStatus newStatus, String triggeredBy, String notes) {
        return BotHistory.builder()
                .bot(bot)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .timestamp(LocalDateTime.now())
                .triggeredBy(triggeredBy)
                .notes(notes)
                .build();
    }

    /**
     * Domain method to create an error record
     */
    public static BotHistory createErrorRecord(Bot bot, Bot.BotStatus previousStatus,
                                               String errorDetails, String triggeredBy) {
        return BotHistory.builder()
                .bot(bot)
                .previousStatus(previousStatus)
                .newStatus(Bot.BotStatus.ERROR)
                .timestamp(LocalDateTime.now())
                .errorDetails(errorDetails)
                .triggeredBy(triggeredBy)
                .build();
    }
}
