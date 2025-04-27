package com.vuog.telebotmanager.application.usecases;

public interface BotLogUseCase {

    void log(Long botId, String chatId, String message);
}
