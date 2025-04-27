package com.vuog.telebotmanager.domain.bot.model;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking bot status history
 */
@Entity
@Table(name = "bot_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "bot_id", nullable = false)
    private TelegramBot bot;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommonEnum.BotStatus previousStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommonEnum.BotStatus newStatus;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(length = 1000)
    private String notes;
    
    // Added for error tracking
    @Column(length = 2000)
    private String errorDetails;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
