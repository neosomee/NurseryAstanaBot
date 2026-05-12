package com.example.nurseryAstana.telegram.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelCommand implements BotCommand {

    private final ReportStateManager stateManager;
    private final TelegramBot telegramBot;

    @Override
    public boolean supports(String command) {
        return "/cancel".equals(command);
    }

    @Override
    public String getCommand() {
        return "/cancel";
    }

    @Override
    public void execute(Long chatId, Message message) {
        stateManager.clearState(chatId);
        telegramBot.execute(new SendMessage(chatId, "Операция отменена. Можешь начать заново."));
    }
}