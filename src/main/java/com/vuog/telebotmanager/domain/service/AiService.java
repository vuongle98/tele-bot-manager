package com.vuog.telebotmanager.domain.service;

import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;

import java.util.Map;

/**
 * Domain service interface for AI operations
 * Follows Clean Architecture by defining service contracts in domain layer
 */
public interface AiService {

    /**
     * Process AI-powered command
     */
    CommandResponse processAiCommand(CommandRequest request);

    /**
     * Generate AI response for given prompt
     */
    String generateResponse(String prompt, Map<String, Object> parameters);

    /**
     * Summarize text using AI
     */
    String summarizeText(String text, Map<String, Object> parameters);

    /**
     * Analyze text using AI
     */
    Map<String, Object> analyzeText(String text, Map<String, Object> parameters);

    /**
     * Generate content using AI
     */
    String generateContent(String prompt, Map<String, Object> parameters);

    /**
     * Answer question using AI
     */
    String answerQuestion(String question, String context, Map<String, Object> parameters);

    /**
     * Check if AI service is available
     */
    boolean isAvailable();

    /**
     * Get AI service configuration
     */
    Map<String, Object> getConfiguration();

    /**
     * Update AI service configuration
     */
    void updateConfiguration(Map<String, Object> configuration);

    /**
     * Get AI model information
     */
    AiModelInfo getModelInfo();

    /**
     * AI model information
     */
    interface AiModelInfo {
        String getModelName();

        String getProvider();

        String getVersion();

        Map<String, Object> getCapabilities();

        boolean isAvailable();
    }
}
