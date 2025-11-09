package com.vuog.telebotmanager.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Domain entity representing command execution history
 * Provides audit trail and performance metrics for command executions
 */
@Entity
@Table(name = "command_executions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class CommandExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "command_id", nullable = false)
    private Command command;

    @Column(name = "execution_id", nullable = false, unique = true)
    private String executionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExecutionStatus status;

    @Column(name = "input_data", columnDefinition = "TEXT")
    private String inputData;

    @Column(name = "output_data", columnDefinition = "TEXT")
    private String outputData;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "metadata", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Domain method to start execution
     */
    public void start() {
        this.status = ExecutionStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * Domain method to complete execution successfully
     */
    public void complete(String outputData) {
        this.status = ExecutionStatus.COMPLETED;
        this.outputData = outputData;
        this.completedAt = LocalDateTime.now();
        calculateExecutionTime();
    }

    /**
     * Domain method to fail execution
     */
    public void fail(String errorMessage) {
        this.status = ExecutionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        calculateExecutionTime();
    }

    /**
     * Domain method to timeout execution
     */
    public void timeout() {
        this.status = ExecutionStatus.TIMEOUT;
        this.completedAt = LocalDateTime.now();
        calculateExecutionTime();
    }

    /**
     * Domain method to cancel execution
     */
    public void cancel() {
        this.status = ExecutionStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
        calculateExecutionTime();
    }

    /**
     * Calculate execution time in milliseconds
     */
    private void calculateExecutionTime() {
        if (startedAt != null && completedAt != null) {
            this.executionTimeMs = java.time.Duration.between(startedAt, completedAt).toMillis();
        }
    }

    /**
     * Check if execution is still running
     */
    public boolean isRunning() {
        return status == ExecutionStatus.RUNNING;
    }

    /**
     * Check if execution completed successfully
     */
    public boolean isSuccessful() {
        return status == ExecutionStatus.COMPLETED;
    }

    /**
     * Check if execution failed
     */
    public boolean isFailed() {
        return status == ExecutionStatus.FAILED || status == ExecutionStatus.TIMEOUT;
    }

    public enum ExecutionStatus {
        PENDING, RUNNING, COMPLETED, FAILED, TIMEOUT, CANCELLED
    }
}
