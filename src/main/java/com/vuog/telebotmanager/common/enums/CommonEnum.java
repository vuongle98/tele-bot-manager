package com.vuog.telebotmanager.common.enums;

public class CommonEnum {

    public enum AlertLevel {
        INFO, WARNING, ERROR, SUCCESS
    }

    public enum BotStatus {
        STARTING,
        CREATED,
        RUNNING,
        STOPPING,
        STOPPED,
        ERRORED
    }

    public enum UpdateMethod {
        WEBHOOK,
        LONG_POLLING
    }
}
