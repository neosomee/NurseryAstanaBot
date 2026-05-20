package com.example.nurseryAstana.telegram.configuration;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class NurseryAstanaConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {

        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(
                                120,
                                TimeUnit.SECONDS
                        )
                        .readTimeout(
                                120,
                                TimeUnit.SECONDS
                        )
                        .writeTimeout(
                                120,
                                TimeUnit.SECONDS
                        )
                        .build();

        TelegramBot telegramBot =
                new TelegramBot.Builder(token)
                        .okHttpClient(client)
                        .build();

        log.info("TelegramBot bean created");

        return telegramBot;
    }
}