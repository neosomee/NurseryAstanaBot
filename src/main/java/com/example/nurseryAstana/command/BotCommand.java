package com.example.nurseryAstana.command;

import com.pengrad.telegrambot.model.Message;

public interface BotCommand {
    boolean supports(String command);
    void execute(Long charId, Message message);
    String getCommand();
}
