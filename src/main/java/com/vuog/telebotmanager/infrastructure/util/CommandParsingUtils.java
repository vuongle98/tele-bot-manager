package com.vuog.telebotmanager.infrastructure.util;

import java.util.ArrayList;
import java.util.List;

public final class CommandParsingUtils {
    private CommandParsingUtils() {}

    public static List<String> tokens(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.isBlank()) return result;
        for (String part : input.trim().split("\\s+")) {
            if (!part.isBlank()) result.add(part.trim());
        }
        return result;
    }

    public static String arg(List<String> tokens, int index, String def) {
        return index < tokens.size() ? tokens.get(index) : def;
    }

    public static Long argLong(List<String> tokens, int index) {
        try {
            if (index < tokens.size()) return Long.valueOf(tokens.get(index));
        } catch (Exception ignored) {}
        return null;
    }
}
