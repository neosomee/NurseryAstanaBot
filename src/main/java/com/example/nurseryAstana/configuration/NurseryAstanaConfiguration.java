package com.example.nurseryAstana.configuration;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NurseryAstanaConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {

        TelegramBot telegramBot = new TelegramBot(token);

        log.info("TelegramBot bean created");

        return telegramBot;
    }
}