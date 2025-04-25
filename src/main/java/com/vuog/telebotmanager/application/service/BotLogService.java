package com.vuog.telebotmanager.application.service;

public interface BotLogService {

    void log(Long botId, String chatId, String message);
}
