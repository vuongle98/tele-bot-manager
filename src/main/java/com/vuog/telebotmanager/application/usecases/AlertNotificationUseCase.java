package com.vuog.telebotmanager.application.usecases;

import com.vuog.telebotmanager.common.enums.CommonEnum;

public interface AlertNotificationUseCase {

    void sendAlert(Long botId, String chatId, String message, CommonEnum.AlertLevel level);

    void sendInfo(Long botId, String chatId, String message);

    void sendWarning(Long botId, String chatId, String message);

    void sendError(Long botId, String chatId, String message);

    void sendSuccess(Long botId, String chatId, String message);

}
