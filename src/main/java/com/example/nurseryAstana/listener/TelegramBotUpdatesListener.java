package com.example.nurseryAstana.listener;

import com.example.nurseryAstana.model.Report;
import com.example.nurseryAstana.service.ReportService;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TelegramBotUpdatesListener {

    @Autowired
    private TelegramBot tgBot;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    private final Map<Long, Boolean> waitingForPhoto = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> waitingForText = new ConcurrentHashMap<>();
    private final Map<Long, String> tempPhotoFileIds = new ConcurrentHashMap<>();


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


    private void handleReportCommand(Long chatId) {
        log.info("User {} started report process", chatId);

        String reportInstruction = """
            📝 Отправка обращения / жалобы
            
            Пожалуйста, отправьте:
            1. Фото (одним сообщением)
            2. Затем текстовое описание проблемы
            
            Пример:
            Фото: [ваше фото]
            Текст: "Клетка нуждается в уборке, животные выглядят неопрятно"
            
            Ваше обращение будет рассмотрено администрацией.
            
            Для отмены отправьте /cancel
            """;

        SendMessage request = new SendMessage(chatId, reportInstruction);
        tgBot.execute(request);

        // Отмечаем, что пользователь начал процесс и ждём фото
        waitingForPhoto.put(chatId, true);
        waitingForText.put(chatId, false);
    }

    private void handleReportPhoto(Long chatId, Message message) {
        // Берём самое большое доступное фото (последний элемент в массиве)
        String fileId = message.photo()[message.photo().length - 1].fileId();
        tempPhotoFileIds.put(chatId, fileId);

        log.info("Received photo from chat {}, fileId: {}", chatId, fileId);

        // Теперь ждём текст
        waitingForPhoto.put(chatId, false);
        waitingForText.put(chatId, true);

        SendMessage request = new SendMessage(chatId, "✅ Фото получено! Теперь отправьте текстовое описание проблемы:");
        tgBot.execute(request);
    }

    private void handleReportText(Long chatId, Message message) {
        String description = message.text();
        Long telegramId = message.from().id();
        String photoFileId = tempPhotoFileIds.get(chatId);

        if (photoFileId == null) {
            log.error("No photo found for chat {}", chatId);
            SendMessage request = new SendMessage(chatId, "❌ Ошибка: фото не найдено. Пожалуйста, начните процесс заново с командой /report");
            tgBot.execute(request);
            clearReportState(chatId);
            return;
        }

        // Сохраняем репорт через твой сервис
        Report savedReport = reportService.saveReport(telegramId, description, photoFileId);

        log.info("Report saved successfully: id={}, telegramId={}", savedReport.getId(), telegramId);

        String successMessage = String.format("""
            ✅ Ваше обращение успешно отправлено!
            
            Номер обращения: #%d
            Статус: На рассмотрении
            
            Администрация свяжется с вами в ближайшее время.
            Спасибо за вашу помощь в улучшении нашего питомника! 🐾
            """, savedReport.getId());

        SendMessage request = new SendMessage(chatId, successMessage);
        tgBot.execute(request);

        // Очищаем состояние
        clearReportState(chatId);
    }

    private void clearReportState(Long chatId) {
        waitingForPhoto.remove(chatId);
        waitingForText.remove(chatId);
        tempPhotoFileIds.remove(chatId);
    }
}
