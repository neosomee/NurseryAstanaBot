package com.example.nurseryAstana.telegram.command;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер состояния процесса создания репорта.
 * Хранит временные данные для каждого пользователя: ожидание фото/текста и временный fileId фото.
 * Использует потокобезопасные коллекции для работы в многопоточной среде.
 */
@Component
public class ReportStateManager {

    private final Map<Long, Boolean> waitingForPhoto = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> waitingForText = new ConcurrentHashMap<>();
    private final Map<Long, String> tempPhotoFileIds = new ConcurrentHashMap<>();

    public boolean isWaitingForPhoto(Long chatId) {
        return waitingForPhoto.getOrDefault(chatId, false);
    }

    public boolean isWaitingForText(Long chatId) {
        return waitingForText.getOrDefault(chatId, false);
    }

    public void setWaitingForPhoto(Long chatId, boolean waiting) {
        waitingForPhoto.put(chatId, waiting);
    }

    public void setWaitingForText(Long chatId, boolean waiting) {
        waitingForText.put(chatId, waiting);
    }

    public String getPhotoFileId(Long chatId) {
        return tempPhotoFileIds.get(chatId);
    }

    public void savePhotoFileId(Long chatId, String fileId) {
        tempPhotoFileIds.put(chatId, fileId);
    }

    public void clearState(Long chatId) {
        waitingForPhoto.remove(chatId);
        waitingForText.remove(chatId);
        tempPhotoFileIds.remove(chatId);
    }
}
