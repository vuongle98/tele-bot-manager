package com.vuog.telebotmanager.domain.bot.model;

import com.vuog.telebotmanager.common.enums.CommonEnum;
import com.vuog.telebotmanager.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "telegram_bots")
public class TelegramBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String apiToken;
    
    @Enumerated(EnumType.STRING)
    private CommonEnum.BotStatus status = CommonEnum.BotStatus.CREATED;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private BotConfiguration configuration;

    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    private Boolean scheduled = false;
    
    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "bot_configurations")
    public static class BotConfiguration {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Enumerated(EnumType.STRING)
        private CommonEnum.UpdateMethod updateMethod = CommonEnum.UpdateMethod.LONG_POLLING;
        
        private String webhookUrl;
        private Integer maxConnections = 40;
        private String allowedUpdates = "message,callback_query";

        private boolean isWebhookEnabled = false;
        
        // Polling settings
        private Integer pollingTimeout = 50;
        private Integer pollingLimit = 100;
    }
}