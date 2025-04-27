package com.vuog.telebotmanager.infrastructure.adapters.webhook;

import com.vuog.telebotmanager.infrastructure.bot.BotHandlerRegistry;
import com.vuog.telebotmanager.infrastructure.bot.WebhookBotBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    private final BotHandlerRegistry botHandlerRegistry;

    @PostMapping("/{botId}")
    public ResponseEntity<BotApiMethod<?>> handleWebhookUpdate(
        @PathVariable Long botId,
        @RequestBody Update update
    ) {
        log.debug("Received webhook update for bot {}", botId);
        
        try {
            // Get the bot handler
            WebhookBotBase webhookBot = (WebhookBotBase) botHandlerRegistry.getHandler(botId);
            
            // Process the update
            BotApiMethod<?> response = webhookBot.onWebhookUpdateReceived(update);
            
            // Return the response or 200 OK if no response
            return response != null
                ? ResponseEntity.ok(response)
                : ResponseEntity.ok().build();
        } catch (ClassCastException e) {
            log.error("Bot {} is not a webhook bot", botId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error processing webhook update for bot {}", botId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
