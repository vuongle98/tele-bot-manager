package com.vuog.telebotmanager.domain.valueobject;

public enum UserRole {
    ADMIN(3),
    MODERATOR(2),
    USER(1),
    ANONYMOUS(0);

    private final int level;

    UserRole(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    public boolean atLeast(UserRole other) {
        return this.level >= other.level;
    }
}
