package com.example.nurseryAstana.telegram.command;

import com.example.nurseryAstana.backend.user.service.imple.UserServiceImple;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды /start.
 * Приветствует пользователя, регистрирует его в системе и отображает доступные команды.
 */

@Slf4j
@AllArgsConstructor
@Component
public class StartCommand implements BotCommand {

    private final TelegramBot tgBot;
    private final UserServiceImple userService;

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

        String welcomeText =  "Привет! 👋\n" +
                "\n" +
                "\uD83D\uDC3E Добро пожаловать в питомник «Astana Nursery». \uD83D\uDC3E\n" +
                "Помогаю отправлять отчёты о питомцах.\n" +
                "\n" +
                "Чтобы начать — набери:\n" +
                "/animal\n" +
                "/report\n" +
                "\n" +
                "Если нужно описание всех команд — используй:\n" +
                "/help";

        SendMessage request = new SendMessage(chatId, welcomeText);
        tgBot.execute(request);
        log.info("Sent welcome message to chat {}", chatId);
    }
}
