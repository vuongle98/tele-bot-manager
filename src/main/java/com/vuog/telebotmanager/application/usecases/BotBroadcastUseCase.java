package com.vuog.telebotmanager.application.usecases;

import java.util.List;

public interface BotBroadcastUseCase {

    void broadcastMessage(Long botId, List<String> chatIds, String message);
}
