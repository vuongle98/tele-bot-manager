package com.vuog.telebotmanager.infrastructure.handler;

import com.vuog.telebotmanager.domain.entity.Command;
import com.vuog.telebotmanager.domain.repository.CommandRepository;
import com.vuog.telebotmanager.domain.service.AiService;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command handler for AI-powered commands
 * Handles AI_TASK, AI_ANSWER, SUMMARY, GENERATION, ANALYSIS command types
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AiCommandHandler implements CommandHandler {

    private final AiService aiService;
    private final CommandRepository commandRepository;

    @Override
    public boolean canHandle(CommandRequest request) {
        // This handler can process AI-powered commands from the database
        if (request.getCommand() == null || !request.getCommand().startsWith("/")) {
            return false;
        }

        // Try to find command in database
        Long botId = null;
        try {
            if (request.getBotId() != null) {
                botId = Long.valueOf(request.getBotId());
            }
        } catch (Exception ignored) {
        }

        if (botId != null) {
            List<Command> commands = commandRepository.resolveByBotOrGlobalAndCommand(botId, request.getCommand());
            if (!commands.isEmpty()) {
                Command command = commands.get(0);
                // Handle AI type commands
                return command.isAiPowered() && command.getIsEnabled();
            }
        }

        return false;
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        log.info("Executing AI command: {}", request.getCommand());

        if (!aiService.isAvailable()) {
            return CommandResponse.error(request.getCommandId(), "AI service is not available", "AI_SERVICE_UNAVAILABLE");
        }

        try {
            String responseText = processAiCommand(request);
            return CommandResponse.success(request.getCommandId(), responseText);
        } catch (Exception e) {
            log.error("Error executing AI command: {}", request.getCommand(), e);
            return CommandResponse.error(request.getCommandId(), "AI command execution failed: " + e.getMessage(), "AI_EXECUTION_ERROR");
        }
    }

    @Override
    public String getSupportedCommandType() {
        return "AI_COMMAND";
    }

    @Override
    public int getPriority() {
        return 100; // Lower priority for AI commands
    }

    @Override
    public boolean isAvailable() {
        return aiService.isAvailable();
    }

    private String processAiCommand(CommandRequest request) {
        String command = request.getCommand();
        String inputText = request.getInputText();
        Map<String, Object> parameters = request.getParameters() != null ? request.getParameters() : new HashMap<>();

        if (command.startsWith("/ai") || command.startsWith("/ask")) {
            return aiService.answerQuestion(inputText, request.getContext(), parameters);
        } else if (command.startsWith("/summarize")) {
            return aiService.summarizeText(inputText, parameters);
        } else if (command.startsWith("/generate")) {
            return aiService.generateContent(inputText, parameters);
        } else if (command.startsWith("/analyze")) {
            Map<String, Object> analysis = aiService.analyzeText(inputText, parameters);
            return formatAnalysisResult(analysis);
        } else {
            // Default AI response
            return aiService.generateResponse(inputText, parameters);
        }
    }

    private String formatAnalysisResult(Map<String, Object> analysis) {
        StringBuilder result = new StringBuilder();
        result.append("📊 Analysis Results:\n\n");

        for (Map.Entry<String, Object> entry : analysis.entrySet()) {
            result.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return result.toString();
    }
}
