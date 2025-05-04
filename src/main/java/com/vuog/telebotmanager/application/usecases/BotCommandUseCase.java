package com.vuog.telebotmanager.application.usecases;

import com.vuog.telebotmanager.application.command.CreateBotCommandCommand;
import com.vuog.telebotmanager.application.query.BotCommandQuery;
import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BotCommandUseCase {

    BotCommand createCommand(Long botId, CreateBotCommandCommand command);

    void deleteCommand(Long botId, String commandName);

    void deleteAllCommands(Long botId);

    BotCommand getCommand(Long botId, String commandName);

    BotCommand updateCommand(Long botId, String commandName, CreateBotCommandCommand command);

    List<BotCommand> getAllCommands(Long botId);

    Page<BotCommand> getAllCommands(Long botId, BotCommandQuery query, Pageable pageable);

}
