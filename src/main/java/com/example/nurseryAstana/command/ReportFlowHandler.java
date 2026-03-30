package com.example.nurseryAstana.command;

import com.example.nurseryAstana.service.ReportService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ReportFlowHandler {
    private final TelegramBot tgBot;
    private final ReportService reportService;
    private final ReportStateManager stateManager;

    public void handlePhoto(Long chatId, Message message) {
        String fileId = message.photo()[message.photo().length - 1].fileId();
        stateManager.savePhotoFileId(chatId, fileId);

        log.info("Received photo from chat {}, fileId: {}", chatId, fileId);

        stateManager.setWaitingForPhoto(chatId, false);
        stateManager.setWaitingForText(chatId, true);

        SendMessage request = new SendMessage(chatId, "✅ Фото получено! Теперь отправьте текстовое описание проблемы:");
        tgBot.execute(request);
    }

    public void handleText(Long chatId, Message message) {
        String description = message.text();
        Long telegramId = message.from().id();
        String photoFileId = stateManager.getPhotoFileId(chatId);

        if (photoFileId == null) {
            log.error("No photo found for chat {}", chatId);
            SendMessage request = new SendMessage(chatId, "❌ Ошибка: фото не найдено. Пожалуйста, начните процесс заново с командой /report");
            tgBot.execute(request);
            stateManager.clearState(chatId);
            return;
        }

        var savedReport = reportService.saveReport(telegramId, description, photoFileId);

        log.info("Report saved successfully: id={}, telegramId={}", savedReport.getId(), telegramId);

        String successMessage = String.format("""
            ✅ Ваше обращение успешно отправлено!
            
            Номер обращения: #%d
            Статус: На рассмотрении
            
            Спасибо за вашу помощь в улучшении нашего питомника! 🐾
            """, savedReport.getId());

        SendMessage request = new SendMessage(chatId, successMessage);
        tgBot.execute(request);

        stateManager.clearState(chatId);
    }


}

