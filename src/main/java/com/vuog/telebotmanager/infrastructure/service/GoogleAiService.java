package com.vuog.telebotmanager.infrastructure.service;

import com.vuog.telebotmanager.domain.service.AiService;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.infrastructure.config.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Google AI service implementation using Gemini API
 * Provides AI capabilities for the bot management system
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAiService implements AiService {

    private final Map<String, Object> configuration = new HashMap<>();
    private final AppSettings appSettings;

    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";
    private final NetHttpTransport transport = new NetHttpTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private HttpRequestFactory requestFactory() {
        return transport.createRequestFactory(request -> {
            int timeout = appSettings.getAi().getTimeoutMs();
            request.setConnectTimeout(timeout);
            request.setReadTimeout(timeout);
        });
    }

    @Override
    public CommandResponse processAiCommand(CommandRequest request) {
        log.info("Processing AI command: {}", request.getCommand());

        if (!isAvailable()) {
            return CommandResponse.error(request.getCommandId(), "AI service is not available", "AI_SERVICE_UNAVAILABLE");
        }

        try {
            String responseText = generateResponse(request.getInputText(), request.getParameters());
            return CommandResponse.success(request.getCommandId(), responseText);
        } catch (Exception e) {
            log.error("Error processing AI command", e);
            return CommandResponse.error(request.getCommandId(), "AI processing failed: " + e.getMessage(), "AI_PROCESSING_ERROR");
        }
    }

    @Override
    public String generateResponse(String prompt, Map<String, Object> parameters) {
        log.info("Generating AI response for prompt: {}", prompt);

        if (!isAvailable()) {
            throw new IllegalStateException("AI service is not available");
        }

        try {
            return callGeminiText(prompt, parameters);
        } catch (Exception e) {
            log.error("Error generating AI response", e);
            throw new RuntimeException("Failed to generate AI response", e);
        }
    }

    @Override
    public String summarizeText(String text, Map<String, Object> parameters) {
        log.info("Summarizing text of length: {}", text.length());

        if (!isAvailable()) {
            throw new IllegalStateException("AI service is not available");
        }

        try {
            int maxLength = parameters != null && parameters.containsKey("maxLength")
                    ? Integer.parseInt(parameters.get("maxLength").toString()) : 200;
            String prompt = "Summarize the following text in up to " + maxLength + " characters. Keep key facts.\n\n" + text;
            String result = callGeminiText(prompt, parameters);
            if (result.length() > maxLength) {
                return result.substring(0, maxLength) + "...";
            }
            return result;
        } catch (Exception e) {
            log.error("Error summarizing text", e);
            throw new RuntimeException("Failed to summarize text", e);
        }
    }

    @Override
    public Map<String, Object> analyzeText(String text, Map<String, Object> parameters) {
        log.info("Analyzing text of length: {}", text.length());

        if (!isAvailable()) {
            throw new IllegalStateException("AI service is not available");
        }

        try {
            String analysisPrompt = "Analyze the following text and return a compact JSON object with the fields: " +
                    "wordCount (number), characterCount (number), sentiment (one of: positive, neutral, negative), " +
                    "language (ISO code), complexity (one of: low, medium, high). Only output JSON.\n\n" + text;
            String json = callGeminiText(analysisPrompt, parameters);
            // Parse JSON string into Map
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = jsonFactory.createJsonParser(json).parse(HashMap.class);
            return parsed != null ? parsed : Map.of();
        } catch (Exception e) {
            log.error("Error analyzing text", e);
            throw new RuntimeException("Failed to analyze text", e);
        }
    }

    @Override
    public String generateContent(String prompt, Map<String, Object> parameters) {
        log.info("Generating content for prompt: {}", prompt);

        if (!isAvailable()) {
            throw new IllegalStateException("AI service is not available");
        }

        try {
            return callGeminiText(prompt, parameters);
        } catch (Exception e) {
            log.error("Error generating content", e);
            throw new RuntimeException("Failed to generate content", e);
        }
    }

    @Override
    public String answerQuestion(String question, String context, Map<String, Object> parameters) {
        log.info("Answering question: {}", question);

        if (!isAvailable()) {
            throw new IllegalStateException("AI service is not available");
        }

        try {
            String prompt = (context != null && !context.isBlank() ? ("Context:\n" + context + "\n\n") : "") +
                    "Question: " + question + "\nAnswer concisely and accurately.";
            return callGeminiText(prompt, parameters);
        } catch (Exception e) {
            log.error("Error answering question", e);
            throw new RuntimeException("Failed to answer question", e);
        }
    }

    @Override
    public boolean isAvailable() {
        return appSettings.getAi().isEnabled() && appSettings.getAi().getApiKey() != null && !appSettings.getAi().getApiKey().trim().isEmpty();
    }

    @Override
    public Map<String, Object> getConfiguration() {
        configuration.put("enabled", appSettings.getAi().isEnabled());
        configuration.put("modelName", appSettings.getAi().getModel());
        configuration.put("temperature", appSettings.getAi().getTemperature());
        configuration.put("maxTokens", appSettings.getAi().getMaxTokens());
        configuration.put("timeoutMs", appSettings.getAi().getTimeoutMs());
        return new HashMap<>(configuration);
    }

    @Override
    public void updateConfiguration(Map<String, Object> newConfiguration) {
        configuration.putAll(newConfiguration);
        log.info("AI service configuration updated");
    }

    @Override
    public AiModelInfo getModelInfo() {
        return new AiModelInfo() {
            @Override
            public String getModelName() {
                return appSettings.getAi().getModel();
            }

            @Override
            public String getProvider() {
                return "Google";
            }

            @Override
            public String getVersion() {
                return "1.0.0";
            }

            @Override
            public Map<String, Object> getCapabilities() {
                Map<String, Object> capabilities = new HashMap<>();
                capabilities.put("textGeneration", true);
                capabilities.put("textSummarization", true);
                capabilities.put("textAnalysis", true);
                capabilities.put("questionAnswering", true);
                capabilities.put("contentGeneration", true);
                return capabilities;
            }

            @Override
            public boolean isAvailable() {
                return GoogleAiService.this.isAvailable();
            }
        };
    }

    // Real API call using google-api-client
    private String callGeminiText(String prompt, Map<String, Object> parameters) throws Exception {
        String url = String.format(GEMINI_ENDPOINT, appSettings.getAi().getModel(), appSettings.getAi().getApiKey());

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", parameters != null && parameters.containsKey("temperature")
                ? Double.parseDouble(parameters.get("temperature").toString()) : appSettings.getAi().getTemperature());
        generationConfig.put("maxOutputTokens", parameters != null && parameters.containsKey("maxTokens")
                ? Integer.parseInt(parameters.get("maxTokens").toString()) : appSettings.getAi().getMaxTokens());

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", java.util.List.of(part));
        body.put("contents", java.util.List.of(content));
        body.put("generationConfig", generationConfig);

        byte[] payload = jsonFactory.toByteArray(body);
        HttpRequest request = requestFactory().buildPostRequest(new GenericUrl(url),
                new ByteArrayContent("application/json", payload));
        request.getHeaders().setAccept("application/json");

        HttpResponse response = request.execute();
        try {
            try (java.io.InputStream is = response.getContent()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> resp = jsonFactory.createJsonParser(is).parse(HashMap.class);
                // Extract candidates[0].content.parts[0].text
                Object candidatesObj = resp.get("candidates");
                if (candidatesObj instanceof java.util.List<?> candidates && !candidates.isEmpty()) {
                    Object first = candidates.getFirst();
                    if (first instanceof Map<?, ?> c0) {
                        Object contentObj = c0.get("content");
                        if (contentObj instanceof Map<?, ?> contentMap) {
                            Object partsObj = contentMap.get("parts");
                            if (partsObj instanceof java.util.List<?> parts && !parts.isEmpty()) {
                                Object p0 = parts.getFirst();
                                if (p0 instanceof Map<?, ?> partMap) {
                                    Object textObj = partMap.get("text");
                                    if (textObj != null) return textObj.toString();
                                }
                            }
                        }
                    }
                }
                // Fallback: return entire JSON string
                return resp.toString();
            }
        } finally {
            response.disconnect();
        }
    }
}
