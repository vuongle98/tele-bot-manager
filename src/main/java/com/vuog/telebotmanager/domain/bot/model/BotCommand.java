package com.vuog.telebotmanager.domain.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bot_commands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bot_id", nullable = false)
    private TelegramBot bot;

    @Column(nullable = false)
    private String command;

    @Column(nullable = false, length = 1000)
    private String responseTemplate;

    @Column(nullable = false)
    private Boolean isEnabled = true;

    @Column(length = 255)
    private String description;

    private String handlerMethod;

    // Optional field to store additional configuration as JSON
    @Column(columnDefinition = "TEXT")
    private String additionalConfig;

    public String getCommandWithPrefix() {
        return "/" + command;
    }
    
    @Override
    public String toString() {
        return "BotCommand{" +
                "id=" + id +
                ", bot=" + (bot != null ? bot.getId() : null) +
                ", command='" + command + '\'' +
                ", isEnabled=" + isEnabled +
                ", description='" + description + '\'' +
                '}';
    }
}
