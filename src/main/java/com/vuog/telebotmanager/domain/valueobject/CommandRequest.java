package com.vuog.telebotmanager.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Value object representing a command execution request
 * Immutable data structure for command processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequest {

    private String commandId;
    private String botId;
    private String userId;
    private String chatId;
    private String messageId;
    private String command;
    private String inputText;
    private Map<String, Object> parameters;
    private String triggeredBy;
    private LocalDateTime timestamp;
    private String context;
    private Map<String, Object> metadata;

    /**
     * Create a new command request from basic parameters
     */
    public static CommandRequest create(String commandId, String botId, String userId,
                                        String chatId, String command, String inputText) {
        return CommandRequest.builder()
                .commandId(commandId)
                .botId(botId)
                .userId(userId)
                .chatId(chatId)
                .command(command)
                .inputText(inputText)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Add parameter to the request
     */
    public CommandRequest withParameter(String key, Object value) {
        if (this.parameters == null) {
            this.parameters = new java.util.HashMap<>();
        }
        this.parameters.put(key, value);
        return this;
    }

    /**
     * Add metadata to the request
     */
    public CommandRequest withMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    /**
     * Get parameter value with default
     */
    public Object getParameter(String key, Object defaultValue) {
        return parameters != null ? parameters.getOrDefault(key, defaultValue) : defaultValue;
    }

    /**
     * Get metadata value with default
     */
    public Object getMetadata(String key, Object defaultValue) {
        return metadata != null ? metadata.getOrDefault(key, defaultValue) : defaultValue;
    }
}
