package com.example.nurseryAstana.backend.report.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

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
     * Клиент для скачивания файлов из Telegram.
     * Работает через системный/VPN route.
     */
    private final OkHttpClient downloadClient =
            new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();

    public String saveReportPhoto(
            Long telegramId,
            String fileId
    ) throws Exception {

        GetFile getFile = new GetFile(fileId);

        GetFileResponse fileResponse =
                tgBot.execute(getFile);

        if (!fileResponse.isOk()) {

            throw new RuntimeException(
                    "Не удалось получить file_path из Telegram"
            );
        }

        com.pengrad.telegrambot.model.File tgFile =
                fileResponse.file();

        String filePath = tgFile.filePath();

        String fileUrl =
                "https://api.telegram.org/file/bot"
                        + token
                        + "/"
                        + filePath;

        String localFileName =
                "report-"
                        + telegramId
                        + "-"
                        + System.currentTimeMillis()
                        + ".jpg";

        Path uploadPath =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();

        Files.createDirectories(uploadPath);

        Path target =
                uploadPath.resolve(localFileName);

        log.info("Saving report photo to {}", target);

        Request request = new Request.Builder()
                .url(fileUrl)
                .build();

        try (Response response =
                     downloadClient.newCall(request).execute()) {

            if (!response.isSuccessful()
                    || response.body() == null) {

                throw new RuntimeException(
                        "Ошибка скачивания файла: "
                                + response.code()
                );
            }

            try (InputStream in =
                         response.body().byteStream()) {

                Files.copy(
                        in,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        }

        String localUrl = "/files/" + localFileName;

        log.info(
                "Report photo saved successfully, url={}",
                localUrl
        );

        return localUrl;
    }
}