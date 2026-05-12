package com.example.nurseryAstana.listener;

import com.example.nurseryAstana.telegram.BotCommand;
import com.example.nurseryAstana.telegram.ReportFlowHandler;
import com.example.nurseryAstana.telegram.ReportStateManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Слушатель обновлений от Telegram Bot API.
 * Обрабатывает входящие сообщения, команды и состояния диалога.
 *
 * <p>Основные функции:
 * <ul>
 *   <li>Обработка команд через реализацию паттерна "Команда"</li>
 *   <li>Управление состоянием создания репортов (фото + текст)</li>
 *   <li>Логирование всех входящих обновлений</li>
 * </ul>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener {

    private final TelegramBot tgBot;
    private final List<BotCommand> commands;
    private final ReportStateManager stateManager;
    private final ReportFlowHandler reportFlowHandler;

    private Map<String, BotCommand> commandMap;

    @PostConstruct
    public void init() {

        commandMap = commands.stream()
                .collect(Collectors.toMap(
                        BotCommand::getCommand,
                        Function.identity()
                ));

        log.info("Starting TelegramBotUpdatesListener with commands: {}", commandMap.keySet());

        try {

            tgBot.setUpdatesListener(updates -> {
                for (Update update : updates) {
                    try {
                        handleUpdate(update);
                    } catch (Exception e) {
                        log.error("Error handling update: {}", e.getMessage(), e);
                    }
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });

            log.info("TelegramBotUpdatesListener started successfully");

        } catch (Exception e) {

            log.warn("Telegram API is not available. Listener not started.");
            log.debug("Error: {}", e.getMessage());
        }
    }

    private void handleUpdate(Update update) {
        if (update.message() == null) {
            return;
        }

        Message message = update.message();
        Long chatId = message.chat().id();
        String text = message.text();

        if (text != null && commandMap.containsKey(text)) {
            commandMap.get(text).execute(chatId, message);
            return;
        }

        if (message.photo() != null && stateManager.isWaitingForPhoto(chatId)) {
            reportFlowHandler.handlePhoto(chatId, message);
            return;
        }

        if (text != null && stateManager.isWaitingForText(chatId)) {
            reportFlowHandler.handleText(chatId, message);
            return;
        }

        if (text != null) {
            log.debug("Unknown command or message from chat {}: {}", chatId, text);
        }
    }
}