package com.vuog.telebotmanager.application.usecases;

import com.vuog.telebotmanager.application.command.ScheduleMessageCommand;
import com.vuog.telebotmanager.domain.bot.model.ScheduledMessage;

import java.util.List;

/**
 * Use case for scheduling messages to be sent by a bot
 */
public interface ScheduleMessageUseCase {
    /**
     * Schedule a new message to be sent
     * @param command The message details
     * @return The created scheduled message
     */
    ScheduledMessage scheduleMessage(Long botId, ScheduleMessageCommand command);
    
    /**
     * Cancel a scheduled message
     * @param messageId ID of the message to cancel
     */
    void cancelScheduledMessage(Long botId, Long messageId);
    
    /**
     * Get all scheduled messages for a bot
     * @param botId ID of the bot
     * @return List of scheduled messages
     */
    List<ScheduledMessage> getScheduledMessagesForBot(Long botId);


    /**
     * Cancel scheduled message of bot
     * @param botId Id of the bot
     */
    public void cancelAllScheduledMessageByBot(Long botId);

}
