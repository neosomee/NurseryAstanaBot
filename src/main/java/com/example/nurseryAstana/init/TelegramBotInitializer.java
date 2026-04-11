package com.example.nurseryAstana.init;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeDefault;
import com.pengrad.telegrambot.request.DeleteMyCommands;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotInitializer {

    private final TelegramBot bot;

    @PostConstruct
    public void init() {

        try {

            DeleteMyCommands deleteMyCommands = new DeleteMyCommands()
                    .scope(new BotCommandScopeDefault());

            bot.execute(deleteMyCommands);

            log.info("Telegram commands deleted");

        } catch (Exception e) {

            log.warn("Telegram is not available. Skipping commands initialization");
            log.debug("Error: {}", e.getMessage());
        }
    }
}
