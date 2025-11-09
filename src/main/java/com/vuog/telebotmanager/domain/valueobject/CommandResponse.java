package com.vuog.telebotmanager.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Value object representing a command execution response
 * Immutable data structure for command results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponse {

    private String executionId;
    private String commandId;
    private String botId;
    private String userId;
    private String chatId;
    private String messageId;
    private String responseText;
    private String responseType;
    private Map<String, Object> responseData;
    private boolean success;
    private String errorMessage;
    private String errorCode;
    private LocalDateTime timestamp;
    private Long executionTimeMs;
    private Map<String, Object> metadata;

    /**
     * Create a successful response
     */
    public static CommandResponse success(String executionId, String responseText) {
        return CommandResponse.builder()
                .executionId(executionId)
                .responseText(responseText)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a successful response with data
     */
    public static CommandResponse success(String executionId, String responseText,
                                          Map<String, Object> responseData) {
        return CommandResponse.builder()
                .executionId(executionId)
                .responseText(responseText)
                .responseData(responseData)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response
     */
    public static CommandResponse error(String executionId, String errorMessage, String errorCode) {
        return CommandResponse.builder()
                .executionId(executionId)
                .errorMessage(errorMessage)
                .responseText(errorMessage)
                .errorCode(errorCode)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Add response data
     */
    public CommandResponse withData(String key, Object value) {
        if (this.responseData == null) {
            this.responseData = new java.util.HashMap<>();
        }
        this.responseData.put(key, value);
        return this;
    }

    /**
     * Add metadata
     */
    public CommandResponse withMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    /**
     * Set execution time
     */
    public CommandResponse withExecutionTime(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
        return this;
    }

    /**
     * Check if response is successful
     */
    public boolean isSuccessful() {
        return success;
    }

    /**
     * Check if response has error
     */
    public boolean hasError() {
        return !success && errorMessage != null;
    }
}
