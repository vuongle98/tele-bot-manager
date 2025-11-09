package com.vuog.telebotmanager.infrastructure.handler;

import com.vuog.telebotmanager.application.service.PermissionService;
import com.vuog.telebotmanager.application.service.RoleManagementService;
import com.vuog.telebotmanager.application.usecase.ConfigurationUseCase;
import com.vuog.telebotmanager.application.usecase.PluginManagementUseCase;
import com.vuog.telebotmanager.application.service.BotAdminService;
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
public class AdminCommandHandler implements CommandHandler {

    private final PermissionService permissionService;
    private final RoleManagementService roleManagementService;
    private final PluginManagementUseCase pluginUseCase;
    private final ConfigurationUseCase configurationUseCase;
    private final BotAdminService botAdminService;

    @Override
    public boolean canHandle(CommandRequest request) {
        return request.getCommand() != null && request.getCommand().equals(AdminConstants.ADMIN_CMD);
    }

    @Override
    public CommandResponse execute(CommandRequest request) {
        if (!permissionService.hasRoleAtLeast(request.getUserId(), HandlerUtils.parseBotId(request), UserRole.ADMIN)) {
            return CommandResponse.error(request.getCommandId(), TelegramUtils.formatHtmlText("Permission denied: ADMIN role required"), "FORBIDDEN");
        }

        List<String> tokens = CommandParsingUtils.tokens(request.getInputText());
        if (tokens.isEmpty()) {
            return CommandResponse.success(request.getCommandId(), TelegramUtils.formatHtmlText(adminHelp()));
        }

        if (tokens.size() == 1) {
            return CommandResponse.success(request.getCommandId(), TelegramUtils.formatHtmlText(adminHelp()));
        }

        String area = tokens.get(1).toLowerCase(Locale.ROOT);
        return switch (area) {
            case AdminConstants.AREA_ROLE -> handleRole(tokens, request);
            case AdminConstants.AREA_COMMANDS -> handleCommands(tokens, request);
            case AdminConstants.AREA_PLUGIN -> handlePlugin(tokens, request);
            case AdminConstants.AREA_CONFIG -> handleConfig(tokens, request);
            case AdminConstants.AREA_BOT -> handleBot(tokens, request);
            default -> CommandResponse.success(request.getCommandId(), TelegramUtils.formatHtmlText(adminHelp()));
        };
    }

    private CommandResponse handleRole(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 2, "");
        String userId = CommandParsingUtils.arg(t, 3, "");
        Long botId = CommandParsingUtils.argLong(t, 4); // optional at index 4 for set, 3 for get
        if (AdminConstants.ACT_GET.equalsIgnoreCase(action)) {
            if (botId == null) botId = CommandParsingUtils.argLong(t, 3);
            var role = roleManagementService.getRole(userId, botId);
            return CommandResponse.success(req.getCommandId(), "Role of " + userId + " = " + role);
        } else if (AdminConstants.ACT_SET.equalsIgnoreCase(action)) {
            String roleStr = CommandParsingUtils.arg(t, 4, "");
            UserRole role;
            try { role = UserRole.valueOf(roleStr.toUpperCase(Locale.ROOT)); } catch (Exception e) {
                return CommandResponse.error(req.getCommandId(), TelegramUtils.formatHtmlText("Invalid role: " + roleStr), "BAD_REQUEST");
            }
            var saved = roleManagementService.setRole(userId, botId, role);
            return CommandResponse.success(req.getCommandId(), "Set role for " + userId + " to " + saved.getRole());
        }
        return CommandResponse.success(req.getCommandId(), TelegramUtils.formatHtmlText("Usage: /admin role get <userId> [botId]\n/admin role set <userId> <ADMIN|MODERATOR|USER|ANONYMOUS> [botId]"));
    }

    private CommandResponse handleCommands(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 2, "");
        String userId = CommandParsingUtils.arg(t, 3, "");
        Long botId = CommandParsingUtils.argLong(t, 4);
        if (AdminConstants.ACT_LIST.equalsIgnoreCase(action)) {
            if (botId == null) botId = CommandParsingUtils.argLong(t, 3);
            String list = roleManagementService.listAllowedCommands(userId, botId);
            return CommandResponse.success(req.getCommandId(), list == null || list.isBlank() ? "(empty)" : list);
        } else if (AdminConstants.ACT_ALLOW.equalsIgnoreCase(action) || AdminConstants.ACT_DISALLOW.equalsIgnoreCase(action)) {
            String csv = CommandParsingUtils.arg(t, 3, "");
            boolean add = AdminConstants.ACT_ALLOW.equalsIgnoreCase(action);
            roleManagementService.allowCommands(userId, botId, csv, add);
            return CommandResponse.success(req.getCommandId(), (add ? "Allowed: " : "Disallowed: ") + csv);
        }
        return CommandResponse.success(req.getCommandId(), TelegramUtils.formatHtmlText("Usage: /admin commands list <userId> [botId]\n/admin commands allow <userId> <csvCommands> [botId]\n/admin commands disallow <userId> <csvCommands> [botId]"));
    }

    private CommandResponse handlePlugin(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 2, "");
        if (AdminConstants.ACT_LIST.equalsIgnoreCase(action)) {
            var page = pluginUseCase.getAllPlugins(org.springframework.data.domain.PageRequest.of(0, 100));
            String body = com.vuog.telebotmanager.infrastructure.util.HandlerUtils.formatPluginList(page);
            return CommandResponse.success(req.getCommandId(), body);
        } else if (AdminConstants.ACT_COMPILE.equalsIgnoreCase(action)) {
            String pluginId = CommandParsingUtils.arg(t, 3, "");
            var compiled = pluginUseCase.compilePlugin(pluginId);
            return CommandResponse.success(req.getCommandId(), "Compiled: " + compiled.getId());
        } else if (AdminConstants.ACT_LOAD.equalsIgnoreCase(action)) {
            String pluginId = CommandParsingUtils.arg(t, 3, "");
            var loaded = pluginUseCase.loadPlugin(pluginId);
            return CommandResponse.success(req.getCommandId(), "Loaded: " + loaded.getId());
        }
        return CommandResponse.success(req.getCommandId(), TelegramUtils.formatHtmlText("Usage: /admin plugin list | compile <pluginId> | load <pluginId>"));
    }

    private CommandResponse handleConfig(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 2, "");
        if (AdminConstants.ACT_GET.equalsIgnoreCase(action)) {
            String key = CommandParsingUtils.arg(t, 3, "");
            String val = configurationUseCase.getConfigurationValue(key).orElse("(not found)");
            return CommandResponse.success(req.getCommandId(), key + " = " + val);
        } else if (AdminConstants.ACT_SET.equalsIgnoreCase(action)) {
            String key = CommandParsingUtils.arg(t, 3, "");
            String val = CommandParsingUtils.arg(t, 4, "");
            configurationUseCase.setConfigurationValue(key, val);
            return CommandResponse.success(req.getCommandId(), "Updated " + key);
        }
        return CommandResponse.success(req.getCommandId(), TelegramUtils.formatHtmlText("Usage: /admin config get <key> | set <key> <value>"));
    }

    private CommandResponse handleBot(List<String> t, CommandRequest req) {
        String action = CommandParsingUtils.arg(t, 2, "");
        if (AdminConstants.ACT_LIST.equalsIgnoreCase(action)) {
            var page = botAdminService.listBots(org.springframework.data.domain.PageRequest.of(0, 100));
            StringBuilder sb = new StringBuilder();
            page.getContent().forEach(b -> sb.append(b.getId()).append(" ")
                    .append(b.getBotUsername()).append(" ")
                    .append(b.getStatus()).append("\n"));
            return CommandResponse.success(req.getCommandId(), sb.isEmpty() ? "(no bots)" : sb.toString());
        } else if (AdminConstants.ACT_GET.equalsIgnoreCase(action)) {
            Long botId = CommandParsingUtils.argLong(t, 3);
            if (botId == null) return CommandResponse.error(req.getCommandId(), "botId required", "BAD_REQUEST");
            var bot = botAdminService.getBot(botId).orElse(null);
            if (bot == null) return CommandResponse.error(req.getCommandId(), "Bot not found", "NOT_FOUND");
            String info = bot.getId() + " " + bot.getBotUsername() + " " + bot.getStatus();
            return CommandResponse.success(req.getCommandId(), info);
        } else if (AdminConstants.ACT_ACTIVATE.equalsIgnoreCase(action)) {
            Long botId = CommandParsingUtils.argLong(t, 3);
            if (botId == null) return CommandResponse.error(req.getCommandId(), "botId required", "BAD_REQUEST");
            botAdminService.activate(botId);
            return CommandResponse.success(req.getCommandId(), "Bot activated: " + botId);
        } else if (AdminConstants.ACT_DEACTIVATE.equalsIgnoreCase(action)) {
            Long botId = CommandParsingUtils.argLong(t, 3);
            if (botId == null) return CommandResponse.error(req.getCommandId(), "botId required", "BAD_REQUEST");
            botAdminService.deactivate(botId);
            return CommandResponse.success(req.getCommandId(), "Bot deactivated: " + botId);
        } else if (AdminConstants.ACT_DELETE.equalsIgnoreCase(action)) {
            Long botId = CommandParsingUtils.argLong(t, 3);
            if (botId == null) return CommandResponse.error(req.getCommandId(), "botId required", "BAD_REQUEST");
            botAdminService.delete(botId);
            return CommandResponse.success(req.getCommandId(), "Bot deleted: " + botId);
        }
        return CommandResponse.success(req.getCommandId(), TelegramUtils.formatHtmlText("Usage: /admin bot list | get <botId> | activate <botId> | deactivate <botId> | delete <botId>"));
    }

    @Override
    public String getSupportedCommandType() { return "ADMIN_COMMAND"; }

    @Override
    public int getPriority() { return 10; }

    @Override
    public boolean isAvailable() { return true; }

    private String adminHelp() {
        return """
                🛠 Admin commands:
                /admin role get <userId> [botId]
                /admin role set <userId> <ADMIN|MODERATOR|USER|ANONYMOUS> [botId]
                /admin commands list <userId> [botId]
                /admin commands allow <userId> <csvCommands> [botId]
                /admin commands disallow <userId> <csvCommands> [botId]
                /admin plugin list | compile <pluginId> | load <pluginId>
                /admin config get <key> | set <key> <value>
                /admin bot list | get <botId> | activate <botId> | deactivate <botId> | delete <botId>""";
    }

    @Override
    public java.util.List<String> getSupportedCommands() {
        return java.util.List.of(
                "/admin role get <userId> [botId]",
                "/admin role set <userId> <ROLE> [botId]",
                "/admin commands list <userId> [botId]",
                "/admin commands allow <userId> <csv> [botId]",
                "/admin commands disallow <userId> <csv> [botId]",
                "/admin plugin list | compile <pluginId> | load <pluginId>",
                "/admin config get <key> | set <key> <value>",
                "/admin bot list | get <botId> | activate <botId> | deactivate <botId> | delete <botId>"
        );
    }
}
