package com.example.nurseryAstana.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMyCommands;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeDefault;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NurseryAstanaConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot telegramBot = new TelegramBot(token);

        DeleteMyCommands deleteMyCommands = new DeleteMyCommands()
                .scope(new BotCommandScopeDefault());
        telegramBot.execute(deleteMyCommands);
        return telegramBot;
    }

}
