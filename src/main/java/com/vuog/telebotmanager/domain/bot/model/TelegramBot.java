package com.vuog.telebotmanager.domain.bot.model;

import com.vuog.telebotmanager.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "telegram_bot")
@Getter
@Setter
@NoArgsConstructor
public class TelegramBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "api_token", nullable = false)
    private String apiToken;
    
    @Embedded
    private BotConfiguration configuration;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BotStatus status = BotStatus.STOPPED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String description;

    private Boolean scheduled;

    // Domain methods
    public void start() {
        this.status = BotStatus.STARTING;
    }

    public void stop() {
        this.status = BotStatus.STOPPING;
    }

    public void updateConfiguration(BotConfiguration newConfig) {
        this.configuration = newConfig;
    }

    public enum BotStatus {
        STARTING, RUNNING, STOPPING, STOPPED, ERRORED
    }
    
    public enum UpdateMethod {
        LONG_POLLING,
        WEBHOOK
    }


    @Embeddable
    @Getter
    @Setter
    public static class BotConfiguration {
        @Enumerated(EnumType.STRING)
        private UpdateMethod updateMethod = UpdateMethod.LONG_POLLING;
        
        @URL
        private String webhookUrl;
        
        @Min(1) @Max(100)
        private Integer maxConnections = 40;
        
        private String allowedUpdates = "message,callback_query";
        
        private String ipAddress;
        
        @Column(name = "drop_pending_updates")
        private Boolean dropPendingUpdates = false;
        
        private String secretToken;
        
        private Integer maxThreads;
    }
}
