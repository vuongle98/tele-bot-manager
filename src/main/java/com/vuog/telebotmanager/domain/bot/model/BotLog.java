package com.vuog.telebotmanager.domain.bot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bot_logs")
@AllArgsConstructor
@NoArgsConstructor
public class BotLog implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Long botId;
    private String chatId;
    private String message;
    private LocalDateTime timestamp;
}