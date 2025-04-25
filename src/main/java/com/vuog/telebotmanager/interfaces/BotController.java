package com.vuog.telebotmanager.interfaces;

import com.vuog.telebotmanager.application.command.CreateBotRequest;
import com.vuog.telebotmanager.application.command.ScheduleMessageRequest;
import com.vuog.telebotmanager.application.command.UpdateBotConfigRequest;
import com.vuog.telebotmanager.application.service.impl.BotServiceImpl;
import com.vuog.telebotmanager.common.dto.ApiResponse;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bots")
@RequiredArgsConstructor
public class BotController {
    private final BotServiceImpl botServiceImpl;

    @PostMapping
    public ResponseEntity<ApiResponse<TelegramBot>> createBot(@RequestBody CreateBotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.createBot(request)));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startBot(@PathVariable Long id) throws TelegramApiException {
        botServiceImpl.startBot(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stopBot(@PathVariable Long id) {
        botServiceImpl.stopBot(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<BotStatusResponse>>> getAllStatuses() {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.getAllBotStatuses()));
    }

    @PatchMapping("/{id}/config")
    public ResponseEntity<Void> updateBotConfig(
        @PathVariable Long id,
        @Valid @RequestBody UpdateBotConfigRequest request
    ) throws TelegramApiException {
        botServiceImpl.updateBotConfiguration(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BotResponseDto>>> getAllBots(
            Pageable pageable
    ) {
        Page<BotResponseDto> bots = botServiceImpl.getBotsPageable(pageable).map(BotResponseDto::fromEntity);
        return ResponseEntity.ok(
                ApiResponse.success(bots)
        );
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<ScheduledMessageResponseDto>> scheduleMessage(
        @PathVariable Long id,
        @RequestBody ScheduleMessageRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(ScheduledMessageResponseDto.fromEntity(botServiceImpl.scheduleMessage(id, request)))
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ScheduledMessageResponseDto>> cancelMessage(
            @PathVariable Long id,
            @RequestBody ScheduleMessageRequest request
    ) {
        botServiceImpl.cancelScheduledMessage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/scheduled-messages")
    public ResponseEntity<ApiResponse<List<ScheduledMessageResponseDto>>> getScheduledMessages(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(botServiceImpl.getScheduledMessages(id).stream()
                .map(ScheduledMessageResponseDto::fromEntity)
                .toList())
        );
    }

    @DeleteMapping("/scheduled-messages/{messageId}/cancel")
    public ResponseEntity<Void> cancelScheduledMessage(@PathVariable Long messageId) {
        botServiceImpl.cancelScheduledMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
