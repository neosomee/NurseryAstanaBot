package com.example.nurseryAstana.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Сервис для работы с фото в репортах.
 * <p>
 * Скачивает фотографии по {@code file_id} из Telegram Bot API,
 * сохраняет их в файловую систему и возвращает локальный URL файла.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportPhotoService {

    private final TelegramBot tgBot;

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * Сохраняет фото репорта в файловую систему.
     * <p>
     * Последовательность:
     * <ol>
     *     <li>Получение {@code file_path} по {@code fileId} через Telegram Bot API.</li>
     *     <li>Скачивание файла по URL {@code https://api.telegram.org/file/bot<TOKEN>/<file_path>}.</li>
     *     <li>Сохранение файла в директорию {@code app.upload-dir}.</li>
     *     <li>Возврат относительного URL вида {@code /files/<имя-файла>} для использования в API.</li>
     * </ol>
     *
     * @param telegramId идентификатор пользователя Telegram, используется в имени файла
     * @param fileId     идентификатор файла в Telegram (file_id)
     * @return относительный URL сохранённого файла (например, {@code /files/report-<id>.jpg})
     * @throws Exception если не удалось получить файл у Telegram или сохранить его локально
     */
    public String saveReportPhoto(Long telegramId, String fileId) throws Exception {
        GetFile getFile = new GetFile(fileId);
        GetFileResponse fileResponse = tgBot.execute(getFile);
        com.pengrad.telegrambot.model.File tgFile = fileResponse.file();
        String filePath = tgFile.filePath();

        String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + filePath;

        String localFileName = "report-" + telegramId + "-" + System.currentTimeMillis() + ".jpg";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        Path target = uploadPath.resolve(localFileName);

        log.info("Saving report photo to {}", target);

        try (InputStream in = new URL(fileUrl).openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String localUrl = "/files/" + localFileName;
        log.info("Report photo saved, url={}", localUrl);
        return localUrl;
    }
}