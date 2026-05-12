package com.example.nurseryAstana.telegram.command;

import com.pengrad.telegrambot.model.Message;

/**
 * Интерфейс для всех команд бота.
 * Реализует паттерн "Команда" для обработки различных команд Telegram бота.
 */
public interface BotCommand {
    boolean supports(String command);
    void execute(Long chatId, Message message);
    String getCommand();
}
