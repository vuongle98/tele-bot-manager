package com.vuog.telebotmanager.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Request DTO for scheduling a message
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMessageRequest {
    
    @NotBlank(message = "Chat ID is required")
    private String chatId;
    
    @NotBlank(message = "Message text is required")
    private String messageText;
    
    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledTime;
    
    private Boolean isRecurring = false;
    
    // Only required if isRecurring is true
    private Duration recurrenceInterval;
}
