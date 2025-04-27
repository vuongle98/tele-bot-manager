package com.vuog.telebotmanager.domain.bot.repository;

//import com.vuog.telebotmanager.domain.bot.model.BotCommand;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Repository for managing bot commands
// */
//@Repository
//public interface BotCommandRepository extends JpaRepository<BotCommand, Long> {
//
//    /**
//     * Find all commands for a specific bot
//     *
//     * @param botId The ID of the bot
//     * @return List of commands for the bot
//     */
//    List<BotCommand> findByBotId(Long botId);
//
//    /**
//     * Find a specific command for a bot
//     *
//     * @param botId The ID of the bot
//     * @param command The command string
//     * @return Optional containing the command if found
//     */
//    Optional<BotCommand> findByBotIdAndCommand(Long botId, String command);
//
//    /**
//     * Find all enabled commands for a bot
//     *
//     * @param botId The ID of the bot
//     * @param isEnabled Whether the command is enabled
//     * @return List of enabled commands
//     */
//    List<BotCommand> findByBotIdAndIsEnabled(Long botId, Boolean isEnabled);
//}
import com.vuog.telebotmanager.domain.bot.model.BotCommand;
import com.vuog.telebotmanager.domain.bot.model.TelegramBot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotCommandRepository extends JpaRepository<BotCommand, Long> {

    List<BotCommand> findAllByBotId(Long botId);

    List<BotCommand> findAllByIsEnabledTrueAndBotId(Long botId);

    Optional<BotCommand> findAllByIsEnabledTrueAndBotIdAndCommand(Long botId, String command);

    Long bot(TelegramBot bot);

    Page<BotCommand> findAll(Specification<BotCommand> spec, Pageable pageable);
}
