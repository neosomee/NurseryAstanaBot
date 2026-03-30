package com.example.nurseryAstana.listener;

import com.example.nurseryAstana.service.UserService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TelegramBotUpdatesListener {

    @Autowired
    private TelegramBot tgBot;

    @Autowired
    private UserService userService;


    @PostConstruct
    public void init() {
        log.info("init TelegramBotUpdatesListener");

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

        };

    private void handleUpdate(Update update) {
        // Проверяем, есть ли сообщение
        if (update.message() == null) {
            return;
        }

        Message message = update.message();
        Long chatId = message.chat().id();
        String text = message.text();

        // Проверяем, что это текстовая команда
        if (text == null) {
            return;
        }

        // Обрабатываем команду /start
        if (text.equals("/start")) {
            handleStartCommand(chatId, message);
        }
    }


    private void handleStartCommand(Long chatId, Message message) {
        // Получаем данные пользователя из Telegram
        Long telegramId = message.from().id();
        String username = message.from().username();

        // Создаём или получаем пользователя в базе данных
        userService.createOrGetUser(telegramId, username);
        log.info("User created/found: telegramId={}, username={}", telegramId, username);

        // Отправляем приветственное сообщение
        String welcomeText = """
            🐾 Добро пожаловать в питомник "Nursery Astana"! 🐾
            
            Доступные команды:
            /start - показать это сообщение
            /help - помощь
            /replace
            
            Чем могу помочь? 🌟
            """;

        SendMessage request = new SendMessage(chatId, welcomeText);
        tgBot.execute(request);
        log.info("Sent welcome message to chat {}", chatId);
    }

}
