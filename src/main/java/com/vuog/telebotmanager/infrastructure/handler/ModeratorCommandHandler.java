package com.vuog.telebotmanager.infrastructure.handler;

import com.vuog.telebotmanager.application.service.PermissionService;
import com.vuog.telebotmanager.application.service.RoleManagementService;
import com.vuog.telebotmanager.application.usecase.ConfigurationUseCase;
import com.vuog.telebotmanager.application.usecase.PluginManagementUseCase;
import com.vuog.telebotmanager.domain.entity.BotPlugin;
import com.vuog.telebotmanager.domain.service.CommandHandler;
import com.vuog.telebotmanager.domain.valueobject.CommandRequest;
import com.vuog.telebotmanager.domain.valueobject.CommandResponse;
import com.vuog.telebotmanager.domain.valueobject.UserRole;
import com.vuog.telebotmanager.infrastructure.util.AdminConstants;
import com.vuog.telebotmanager.infrastructure.util.CommandParsingUtils;
import com.vuog.telebotmanager.infrastructure.util.HandlerUtils;
import com.vuog.telebotmanager.infrastructure.util.TelegramUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModeratorCommandHandler implements CommandHandler {

    private final PermissionService permissionService;
    private final RoleManagementService roleManagementService;
    private final PluginManagementUseCase pluginUseCase;
    private final ConfigurationUseCase configurationUseCase;

    @Override
    public boolean canHandle(CommandRequest request) {
        return request.getCommand() != null && request.getCommand().equals(AdminConstants.MOD_CMD);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        if (!permissionService.hasRoleAtLeast(request.getUserId(), parseBotId(request), UserRole.MODERATOR)) {
            return CommandResponse.error(request.getCommandId(), TelegramUtils.formatHtmlText("Permission denied: MODERATOR role required"), "FORBIDDEN");
        }
        List<String> tokens = CommandParsingUtils.tokens(request.getInputText());
        if (tokens.isEmpty()) return CommandResponse.success(request.getCommandId(), TelegramUtils.formatHtmlText(modHelp()));

        if (tokens.size() == 1) {
            return CommandResponse.success(request.getCommandId(), TelegramUtils.formatHtmlText(modHelp()));
        }
        String area = tokens.get(1).toLowerCase(Locale.ROOT);
        return switch (area) {
            case AdminConstants.AREA_ROLE -> handleRole(tokens, request);
            case AdminConstants.AREA_COMMANDS -> handleCommands(tokens, request);
            case AdminConstants.AREA_PLUGIN -> handlePlugin(tokens, request);
            case AdminConstants.AREA_CONFIG -> handleConfig(tokens, request);
            default -> CommandResponse.success(request.getCommandId(), TelegramUtils.formatHtmlText(modHelp()));
        };
    }

    private CommandResponse handleRole(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 1, "");
        if (!AdminConstants.ACT_GET.equalsIgnoreCase(action)) {
            return CommandResponse.error(req.getCommandId(), TelegramUtils.formatHtmlText("Only GET is allowed for role"), "BAD_REQUEST");
        }
        String userId = CommandParsingUtils.arg(t, 2, "");
        Long botId = CommandParsingUtils.argLong(t, 3);
        var role = roleManagementService.getRole(userId, botId);
        return CommandResponse.success(req.getCommandId(), "Role of " + userId + " = " + role);
    }

    private CommandResponse handleCommands(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 1, "");
        if (!AdminConstants.ACT_LIST.equalsIgnoreCase(action)) {
            return CommandResponse.error(req.getCommandId(), TelegramUtils.formatHtmlText("Only LIST is allowed for commands"), "BAD_REQUEST");
        }
        String userId = CommandParsingUtils.arg(t, 2, "");
        Long botId = CommandParsingUtils.argLong(t, 3);
        String list = roleManagementService.listAllowedCommands(userId, botId);
        return CommandResponse.success(req.getCommandId(), list == null || list.isBlank() ? "(empty)" : list);
    }

    private CommandResponse handlePlugin(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 1, "");
        if (AdminConstants.ACT_LIST.equalsIgnoreCase(action)) {
            var page = pluginUseCase.getAllPlugins(org.springframework.data.domain.PageRequest.of(0, 100));
            String body = HandlerUtils.formatPluginList(page);
            return CommandResponse.success(req.getCommandId(), body);
        } else if (AdminConstants.ACT_LOAD.equalsIgnoreCase(action)) {
            String pluginId = CommandParsingUtils.arg(t, 2, "");
            var loaded = pluginUseCase.loadPlugin(pluginId);
            return CommandResponse.success(req.getCommandId(), "Loaded: " + loaded.getId());
        }
        return CommandResponse.success(req.getCommandId(), TelegramUtils.formatHtmlText("Usage: /mod plugin list | load <pluginId>"));
    }

    private CommandResponse handleConfig(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 1, "");
        if (!AdminConstants.ACT_GET.equalsIgnoreCase(action)) {
            return CommandResponse.error(req.getCommandId(), TelegramUtils.formatHtmlText("Only GET is allowed for config"), "BAD_REQUEST");
        }
        String key = CommandParsingUtils.arg(t, 2, "");
        String val = configurationUseCase.getConfigurationValue(key).orElse("(not found)");
        return CommandResponse.success(req.getCommandId(), key + " = " + val);
    }

    @Override
    public String getSupportedCommandType() { return "MODERATOR_COMMAND"; }

    @Override
    public int getPriority() { return 20; }

    @Override
    public boolean isAvailable() { return true; }

    private Long parseBotId(CommandRequest request) {
        try { return request.getBotId() != null ? Long.valueOf(request.getBotId()) : null; } catch (Exception e) { return null; }
    }

    private String modHelp() {
        return """
                🛠 Moderator commands:
                /mod role get <userId> [botId]
                /mod commands list <userId> [botId]
                /mod plugin list | load <pluginId>
                /mod config get <key>""";
    }

    @Override
    public java.util.List<String> getSupportedCommands() {
        return java.util.List.of(
                "/mod role get <userId> [botId]",
                "/mod commands list <userId> [botId]",
                "/mod plugin list | load <pluginId>",
                "/mod config get <key>"
        );
    }
}
