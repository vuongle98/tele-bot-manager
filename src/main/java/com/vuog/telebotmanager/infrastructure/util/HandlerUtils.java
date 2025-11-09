package com.vuog.telebotmanager.infrastructure.util;

import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import org.springframework.data.domain.Page;

public final class HandlerUtils {
    private HandlerUtils() {}

    public static Long parseBotId(CommandRequest request) {
        try {
            return request.getBotId() != null ? Long.valueOf(request.getBotId()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatPluginList(Page<BotPlugin> page) {
        StringBuilder sb = new StringBuilder();
        for (BotPlugin p : page.getContent()) {
            sb.append(p.getId())
              .append(" ")
              .append(p.getName())
              .append(" ")
              .append(p.getStatus())
              .append("\n");
        }
        return sb.isEmpty() ? "(no plugins)" : sb.toString();
    }
}
