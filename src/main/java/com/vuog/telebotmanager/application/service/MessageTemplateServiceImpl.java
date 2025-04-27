package com.vuog.telebotmanager.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for rendering message templates with variable substitution
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTemplateServiceImpl {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    /**
     * Renders a template by substituting variables in the format {{variableName}}
     * with values from the provided map
     *
     * @param template The template string with variables in {{var}} format
     * @param variables Map of variable names to their values
     * @return The rendered template with variables replaced by their values
     */
    public String renderTemplate(String template, Map<String, Object> variables) {
        if (template == null) {
            return "";
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);

        while (matcher.find()) {
            String varName = matcher.group(1).trim();
            Object value = variables.get(varName);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}