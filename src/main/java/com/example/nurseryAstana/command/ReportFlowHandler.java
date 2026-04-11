package com.example.nurseryAstana.command;

import com.example.nurseryAstana.service.ReportPhotoService;
import com.example.nurseryAstana.service.ReportService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Обработчик потока создания репорта.
 * <p>
 * Отвечает за шаги:
 * <ol>
 *     <li>Получение фото и сохранение временного {@code file_id}.</li>
 *     <li>Получение текстового описания.</li>
 *     <li>Скачивание фото, сохранение его в файловую систему.</li>
 *     <li>Создание репорта в базе данных с ссылкой на локальный файл.</li>
 * </ol>
 */
@Component
@Slf4j
@AllArgsConstructor
public class ReportFlowHandler {

    private final TelegramBot tgBot;
    private final ReportService reportService;
    private final ReportStateManager stateManager;
    private final ReportPhotoService reportPhotoService;

    /**
     * Обрабатывает входящее фото на шаге создания репорта.
     * <p>
     * Сохраняет {@code file_id} фотографии во временное состояние и
     * переключает пользователя в режим ожидания текстового описания.
     *
     * @param chatId  идентификатор чата Telegram
     * @param message сообщение с фотографией
     */
    public void handlePhoto(Long chatId, Message message) {
        log.info("handlePhoto called for chatId={}", chatId);

        String fileId = message.photo()[message.photo().length - 1].fileId();
        stateManager.savePhotoFileId(chatId, fileId);

        log.info("Received photo from chat {}, fileId: {}", chatId, fileId);

        stateManager.setWaitingForPhoto(chatId, false);
        stateManager.setWaitingForText(chatId, true);

        SendMessage request = new SendMessage(
                chatId,
                "✅ Фото получено! Теперь отправьте текстовое описание проблемы:"
        );
        tgBot.execute(request);
    }

    /**
     * Обрабатывает текстовое описание репорта и завершает создание обращения.
     * <p>
     * Выполняет:
     * <ul>
     *     <li>Проверку наличия ранее полученного фото.</li>
     *     <li>Скачивание файла по {@code file_id} и сохранение на диск.</li>
     *     <li>Создание репорта в БД с путём к локальному файлу.</li>
     *     <li>Отправку подтверждения пользователю.</li>
     * </ul>
     *
     * @param chatId  идентификатор чата Telegram
     * @param message сообщение с текстовым описанием проблемы
     */
    public void handleText(Long chatId, Message message) {
        log.info("handleText called for chatId={}", chatId);

        String description = message.text();
        Long telegramId = message.from().id();
        String photoFileId = stateManager.getPhotoFileId(chatId);

        if (photoFileId == null) {
            log.error("No photo found for chat {}", chatId);
            SendMessage request = new SendMessage(
                    chatId,
                    "❌ Ошибка: фото не найдено. Пожалуйста, начните процесс заново с командой /report"
            );
            tgBot.execute(request);
            stateManager.clearState(chatId);
            return;
        }

        try {
            String localUrl = reportPhotoService.saveReportPhoto(telegramId, photoFileId);

            var savedReport = reportService.saveReport(telegramId, description, localUrl);

            log.info(
                    "Report saved successfully: id={}, telegramId={}, photoPath={}",
                    savedReport.getId(), telegramId, localUrl
            );

            String successMessage = String.format("""
                ✅ Ваше обращение успешно отправлено!
                
                Номер обращения: #%d
                Статус: На рассмотрении
                
                Спасибо за вашу помощь в улучшении нашего питомника! 🐾
                """, savedReport.getId());

            SendMessage request = new SendMessage(chatId, successMessage);
            tgBot.execute(request);

        } catch (Exception e) {
            log.error("Failed to download or save photo for chat {}: {}", chatId, e.getMessage(), e);
            SendMessage request = new SendMessage(
                    chatId,
                    "❌ Ошибка при сохранении фото. Попробуйте ещё раз позже."
            );
            tgBot.execute(request);
        } finally {
            stateManager.clearState(chatId);
        }
    }
}