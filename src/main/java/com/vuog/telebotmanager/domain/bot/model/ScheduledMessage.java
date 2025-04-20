package com.vuog.telebotmanager.domain.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheduled_messages")
public class ScheduledMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private TelegramBot bot;
    
    private String chatId; // Could be group/channel/user ID
    private String messageText;
    private LocalDateTime scheduledTime;
    private Boolean isRecurring;
    private Duration recurrenceInterval;
    private Boolean isSent;
}