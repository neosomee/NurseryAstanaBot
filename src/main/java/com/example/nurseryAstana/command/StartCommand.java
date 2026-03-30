package com.example.nurseryAstana.command;

import com.example.nurseryAstana.service.UserService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StartCommand implements BotCommand {

    private final TelegramBot tgBot;
    private final UserService userService;

    @Override
    public boolean supports(String command) {
        return "/start".equals(command);
    }

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void execute(Long chatId, Message message) {
        Long telegramId = message.from().id();
        String username = message.from().username();

        userService.createOrGetUser(telegramId, username);
        log.info("User created/found: telegramId={}, username={}", telegramId, username);

        String welcomeText = """
            🐾 Добро пожаловать в питомник "Nursery Astana"! 🐾
            
            Доступные команды:
            /start - показать это сообщение
            /help - помощь
            /report - отправить обращение
            
            Чем могу помочь? 🌟
            """;

        SendMessage request = new SendMessage(chatId, welcomeText);
        tgBot.execute(request);
        log.info("Sent welcome message to chat {}", chatId);
    }
}
