package com.example.nurseryAstana.telegram.listener;

import com.example.nurseryAstana.telegram.command.AnimalCommand;
import com.example.nurseryAstana.telegram.command.BotCommand;
import com.example.nurseryAstana.telegram.command.ReportFlowHandler;
import com.example.nurseryAstana.telegram.command.ReportStateManager;
import com.example.nurseryAstana.telegram.service.TelegramAnimalAdoptionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotUpdatesListener {

    private final TelegramBot tgBot;
    private final List<BotCommand> commands;
    private final ReportStateManager stateManager;
    private final ReportFlowHandler reportFlowHandler;
    private final TelegramAnimalAdoptionService animalAdoptionService;

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
        if (update.callbackQuery() != null) {
            handleCallbackQuery(update.callbackQuery());
            return;
        }

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

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        if (callbackQuery.message() == null || callbackQuery.message().chat() == null) {
            return;
        }

        Long chatId = callbackQuery.message().chat().id();
        String data = callbackQuery.data();

        if (AnimalCommand.isTakeCallback(data)) {
            handleTakeAnimalCallback(callbackQuery, chatId);
            return;
        }

        if (AnimalCommand.isDeclineCallback(data)) {
            handleDeclineAnimalCallback(callbackQuery, chatId);
        }
    }

    private void handleTakeAnimalCallback(CallbackQuery callbackQuery, Long chatId) {
        Long telegramId = resolveTelegramId(callbackQuery, chatId);

        if (animalAdoptionService.findCurrentAdoption(telegramId).isPresent()) {
            tgBot.execute(new SendMessage(chatId, "У вас уже есть закрепленное животное"));
            return;
        }

        Long animalId = AnimalCommand.animalIdFromCallback(callbackQuery.data());
        try {
            animalAdoptionService.takeAnimal(telegramId, animalId);
            removeInlineButtons(callbackQuery);
            tgBot.execute(new SendMessage(chatId, "Животное успешно закреплено за вами"));
        } catch (IllegalStateException e) {
            tgBot.execute(new SendMessage(chatId, e.getMessage()));
        }
    }

    private void handleDeclineAnimalCallback(CallbackQuery callbackQuery, Long chatId) {
        Long telegramId = resolveTelegramId(callbackQuery, chatId);

        if (animalAdoptionService.findCurrentAdoption(telegramId).isPresent()) {
            tgBot.execute(new SendMessage(chatId, "После закрепления животного отказ через эту кнопку невозможен"));
            return;
        }

        removeInlineButtons(callbackQuery);
        updateAnimalMessage(callbackQuery, "Вы отказались от этого животного.\n\n" + safeMessageText(callbackQuery));
        tgBot.execute(new SendMessage(chatId, "Хорошо, тогда посмотрите других животных"));
    }

    private Long resolveTelegramId(CallbackQuery callbackQuery, Long chatId) {
        return callbackQuery.from() != null ? callbackQuery.from().id() : chatId;
    }

    private void removeInlineButtons(CallbackQuery callbackQuery) {
        Message message = callbackQuery.message();
        if (message != null && message.messageId() != null) {
            tgBot.execute(new EditMessageReplyMarkup(message.chat().id(), message.messageId())
                    .replyMarkup(new InlineKeyboardMarkup()));
        }
    }

    private void updateAnimalMessage(CallbackQuery callbackQuery, String text) {
        Message message = callbackQuery.message();
        if (message != null && message.messageId() != null) {
            tgBot.execute(new EditMessageText(message.chat().id(), message.messageId(), text)
                    .replyMarkup(new InlineKeyboardMarkup()));
        }
    }

    private String safeMessageText(CallbackQuery callbackQuery) {
        Message message = callbackQuery.message();
        return message != null && message.text() != null ? message.text() : "";
    }
}
