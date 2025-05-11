package com.vuog.telebotmanager.interfaces;

import com.vuog.telebotmanager.application.command.CreateBotCommand;
import com.vuog.telebotmanager.application.command.CreateBotCommandCommand;
import com.vuog.telebotmanager.application.command.ScheduleMessageCommand;
import com.vuog.telebotmanager.application.command.UpdateBotConfigCommand;
import com.vuog.telebotmanager.application.service.BotUseCaseImpl;
import com.vuog.telebotmanager.application.usecases.BotCommandUseCase;
import com.vuog.telebotmanager.application.usecases.BotHistoryUseCase;
import com.vuog.telebotmanager.application.usecases.BotUseCase;
import com.vuog.telebotmanager.application.usecases.ScheduleMessageUseCase;
import com.vuog.telebotmanager.common.dto.ApiResponse;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import com.vuog.telebotmanager.application.dto.*;
import com.vuog.telebotmanager.interfaces.dto.query.BotQuery;
import com.vuog.telebotmanager.interfaces.dto.request.UpdateBotRequest;
import com.vuog.telebotmanager.interfaces.dto.response.BotDetailResponseDto;
import com.vuog.telebotmanager.interfaces.dto.response.BotHistoryResponseDto;
import com.vuog.telebotmanager.interfaces.rest.dto.BotStatistic;
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
    private final BotUseCase botService;
    private final BotHistoryUseCase botHistoryUseCase;
    private final ScheduleMessageUseCase scheduleMessageUseCase;
    private final BotCommandUseCase botCommandUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<TelegramBot>> createBot(@RequestBody CreateBotCommand request) {
        return ResponseEntity.ok(ApiResponse.success(botService.createBot(request)));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startBot(@PathVariable Long id) throws TelegramApiException {
        botService.startBot(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BotResponseDto>>> getAllBots(
            BotQuery query,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(botService.getBots(query, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BotDetailResponseDto>> getBotDetails(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(botService.getBotDetails(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BotResponseDto>> updateBot(
            @PathVariable Long id,
            @RequestBody UpdateBotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(botService.updateBot(id, request)));
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<Void> refreshBotStatus(@PathVariable Long id) {
        botService.refreshBotStatus(id);
        return ResponseEntity.accepted().build();
    }

//    @GetMapping("/{id}/history")
//    public ResponseEntity<ApiResponse<Page<BotHistoryResponseDto>>> getBotStatusHistory(
//            @PathVariable Long id,
//            Pageable pageable) {
//        return ResponseEntity.ok(ApiResponse.success(botService.getAllByBot(id, pageable)));
//    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<BotHistoryResponseDto>>> getBotStatusHistory(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(botHistoryUseCase.getAllByBot(id)));
    }


    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stopBot(@PathVariable Long id) throws TelegramApiException {
        botService.stopBot(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<BotStatusResponse>>> getAllStatuses() {
        return ResponseEntity.ok(ApiResponse.success(botService.getAllBotStatuses()));
    }

    @PatchMapping("/{id}/config")
    public ResponseEntity<Void> updateBotConfig(
        @PathVariable Long id,
        @Valid @RequestBody UpdateBotConfigCommand request
    ) throws TelegramApiException {
        botService.updateBotConfiguration(id, request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/restart")
    public ResponseEntity<Void> restartBot(@PathVariable Long id) throws TelegramApiException {
        botService.restartBot(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/command")
    public ResponseEntity<ApiResponse<BotCommandDto>> createCommand(
            @PathVariable Long id,
            @RequestBody CreateBotCommandCommand request
    ) {
        BotCommandDto command = new BotCommandDto(botCommandUseCase.createCommand(id, request));

        return ResponseEntity.ok(ApiResponse.success(command));
    }

    @GetMapping("/{id}/command/{commandName}")
    public ResponseEntity<ApiResponse<BotCommandDto>> getCommand(
            @PathVariable Long id,
            @PathVariable String commandName
    ) {
        BotCommandDto command = new BotCommandDto(botCommandUseCase.getCommand(id, commandName));

        return ResponseEntity.ok(ApiResponse.success(command));
    }

    @PutMapping("/{id}/command/{commandName}")
    public ResponseEntity<ApiResponse<BotCommandDto>> updateCommand(
            @PathVariable Long id,
            @PathVariable String commandName,
            @RequestBody CreateBotCommandCommand request
    ) {
        BotCommandDto updatedCommand = new BotCommandDto(botCommandUseCase.updateCommand(id, commandName, request));

        return ResponseEntity.ok(ApiResponse.success(updatedCommand));
    }

    @DeleteMapping("/{id}/command/{commandName}")
    public ResponseEntity<ApiResponse<Void>> deleteCommand(
            @PathVariable Long id,
            @PathVariable String commandName
    ) {
        botCommandUseCase.deleteCommand(id, commandName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/command")
    public ResponseEntity<ApiResponse<List<BotCommandDto>>> getCommands(
            @PathVariable Long id
    ) {
        List<BotCommandDto> commands = botCommandUseCase.getAllCommands(id).stream().map(BotCommandDto::new).toList();
        return ResponseEntity.ok(ApiResponse.success(commands));
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<ScheduledMessageResponseDto>> scheduleMessage(
        @PathVariable Long id,
        @RequestBody ScheduleMessageCommand request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(ScheduledMessageResponseDto.fromEntity(scheduleMessageUseCase.scheduleMessage(id, request)))
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ScheduledMessageResponseDto>> cancelMessage(
            @PathVariable Long id,
            @RequestBody ScheduleMessageCommand request
    ) {
        scheduleMessageUseCase.cancelAllScheduledMessageByBot(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/scheduled-messages")
    public ResponseEntity<ApiResponse<List<ScheduledMessageResponseDto>>> getScheduledMessages(@PathVariable Long id) {

        List<ScheduledMessageResponseDto> scheduledMessages = scheduleMessageUseCase.getScheduledMessagesForBot(id).stream()
                .map(ScheduledMessageResponseDto::fromEntity)
                .toList();

        return ResponseEntity.ok(
            ApiResponse.success(scheduledMessages)
        );
    }

    @DeleteMapping("/{id}/scheduled-messages/{messageId}/cancel")
    public ResponseEntity<Void> cancelScheduledMessage(
            @PathVariable Long id,
            @PathVariable Long messageId) {
        scheduleMessageUseCase.cancelScheduledMessage(id, messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<BotStatistic>> statistics() {
        BotStatistic statistic = botService.statistics();
        return ResponseEntity.ok(ApiResponse.success(statistic));
    }
}
