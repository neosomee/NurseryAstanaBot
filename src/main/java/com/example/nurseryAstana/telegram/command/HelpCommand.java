package com.example.nurseryAstana.telegram.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommand implements BotCommand {

    private final TelegramBot telegramBot;

    @Override
    public boolean supports(String command) {
        return "/help".equals(command);
    }

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public void execute(Long chatId, Message message) {
        String text = """
                Доступные команды бота питомника для Астаны:
                /start - начать работу с ботом
                /animal - показать животных для усыновления
                /report - отправить отчет о баге (сначала фото, потом текст)
                /cancel - отменить текущее действие
                /help - показать эту справку
                """;
        telegramBot.execute(new SendMessage(chatId, text));
    }
}
