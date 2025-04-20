package com.vuog.telebotmanager.application.service;

import com.vuog.telebotmanager.infrastructure.bot.BotHandler;

public interface BotHandlerRegistry {
    BotHandler getHandler(Long botId);
}
