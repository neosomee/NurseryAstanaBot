package com.example.nurseryAstana.command;

import com.example.nurseryAstana.service.UserService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ReportCommand implements BotCommand {

    private final TelegramBot tgBot;
    private final UserService userService;

    @Override
    public boolean supports(String command) {
        return "/report".equals(command);
    }

    @Override
    public String getCommand() {
        return "/report";
    }

    @Override
    public void execute(Long chatId, Message message) {
        log.info("User {} started report process", chatId);

        String reportInstruction = """
            📝 Отправка обращения / жалобы
            
            Пожалуйста, отправьте:
            1. Фото (одним сообщением)
            2. Затем текстовое описание проблемы
            
            Для отмены отправьте /cancel
            """;

        SendMessage request = new SendMessage(chatId, reportInstruction);
        tgBot.execute(request);
    }
}
