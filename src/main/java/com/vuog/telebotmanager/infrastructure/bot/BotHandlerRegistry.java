package com.vuog.telebotmanager.infrastructure.bot;

/**
 * Interface for registering and managing bot handlers
 */
public interface BotHandlerRegistry {
    /**
     * Gets a bot handler by its ID
     * @param botId ID of the bot
     * @return The bot handler
     */
    BotHandler getHandler(Long botId);
    
    /**
     * Registers a bot handler
     * @param botId ID of the bot
     * @param handler The handler to register
     */
    void registerHandler(Long botId, BotHandler handler);
    
    /**
     * Unregisters a bot handler
     * @param botId ID of the bot
     */
    void unregisterHandler(Long botId);
}
