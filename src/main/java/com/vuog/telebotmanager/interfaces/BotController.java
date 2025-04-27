package com.vuog.telebotmanager.interfaces;

import com.vuog.telebotmanager.application.command.CreateBotCommand;
import com.vuog.telebotmanager.application.command.ScheduleMessageCommand;
import com.vuog.telebotmanager.application.command.UpdateBotConfigCommand;
import com.vuog.telebotmanager.application.service.BotUseCaseImpl;
import com.vuog.telebotmanager.common.dto.ApiResponse;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.application.dto.*;
import com.vuog.telebotmanager.interfaces.dto.query.BotQuery;
import com.vuog.telebotmanager.interfaces.dto.request.UpdateBotRequest;
import com.vuog.telebotmanager.interfaces.dto.response.BotDetailResponseDto;
import com.vuog.telebotmanager.interfaces.dto.response.BotHistoryResponseDto;
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
    private final BotUseCaseImpl botServiceImpl;

    @PostMapping
    public ResponseEntity<ApiResponse<TelegramBot>> createBot(@RequestBody CreateBotCommand request) {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.createBot(request)));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startBot(@PathVariable Long id) throws TelegramApiException {
        botServiceImpl.startBot(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BotResponseDto>>> getAllBots(
            BotQuery query,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.getBots(query, pageable)));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BotDetailResponseDto>> getBotDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.getBotDetails(id)));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BotResponseDto>> updateBot(
            @PathVariable Long id, 
            @RequestBody UpdateBotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.updateBot(id, request)));
    }
    
    @PostMapping("/{id}/refresh")
    public ResponseEntity<Void> refreshBotStatus(@PathVariable Long id) {
        botServiceImpl.refreshBotStatus(id);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<Page<BotHistoryResponseDto>>> getBotStatusHistory(
            @PathVariable Long id,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(botServiceImpl.getAllByBot(id, pageable)));
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
        @Valid @RequestBody UpdateBotConfigCommand request
    ) throws TelegramApiException {
        botServiceImpl.updateBotConfiguration(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("get-paged")
    public ResponseEntity<ApiResponse<Page<BotResponseDto>>> getPageAllBots(
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
        @RequestBody ScheduleMessageCommand request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(ScheduledMessageResponseDto.fromEntity(botServiceImpl.scheduleMessage(id, request)))
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ScheduledMessageResponseDto>> cancelMessage(
            @PathVariable Long id,
            @RequestBody ScheduleMessageCommand request
    ) {
        botServiceImpl.cancelAllScheduledMessageByBot(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/scheduled-messages")
    public ResponseEntity<ApiResponse<List<ScheduledMessageResponseDto>>> getScheduledMessages(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.success(botServiceImpl.getScheduledMessagesForBot(id).stream()
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
