package com.example.nurseryAstana.telegram.command;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.animal.model.Species;
import com.example.nurseryAstana.backend.animal.service.impl.AnimalServiceImple;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnimalCommand implements BotCommand {

    private static final int MAX_DESCRIPTION_LENGTH = 160;

    private final TelegramBot telegramBot;
    private final AnimalServiceImple animalService;

    @Override
    public boolean supports(String command) {
        return "/animal".equals(command);
    }

    @Override
    public String getCommand() {
        return "/animal";
    }

    @Override
    public void execute(Long chatId, Message message) {
        List<AnimalResponse> animals = animalService.findAllAnimal();
        telegramBot.execute(new SendMessage(chatId, formatAnimalsMessage(animals)));
    }

    private String formatAnimalsMessage(List<AnimalResponse> animals) {
        if (animals == null || animals.isEmpty()) {
            return "Пока нет животных для усыновления";
        }

        List<String> blocks = new ArrayList<>();
        for (AnimalResponse animal : animals) {
            blocks.add(formatAnimalBlock(animal));
        }

        return String.join("\n\n", blocks);
    }

    private String formatAnimalBlock(AnimalResponse animal) {
        StringBuilder block = new StringBuilder();
        block.append(speciesEmoji(animal.getSpecies()))
                .append(" ")
                .append(hasText(animal.getName()) ? animal.getName().trim() : speciesLabel(animal.getSpecies()));

        appendLine(block, "Вид", speciesLabel(animal.getSpecies()));
        appendLine(block, "Порода", animal.getBreed());
        appendLine(block, "Возраст", ageLabel(animal.getAge()));
        appendLine(block, "Статус", statusLabel(animal.getStatus()));
        appendLine(block, "Описание", truncate(animal.getDescription()));

        return block.toString();
    }

    private void appendLine(StringBuilder text, String label, String value) {
        if (hasText(value)) {
            text.append("\n")
                    .append(label)
                    .append(": ")
                    .append(value.trim());
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String speciesLabel(Species species) {
        if (species == null) {
            return "Животное";
        }
        return switch (species) {
            case DOG -> "Собака";
            case CAT -> "Кошка";
            case OTHER -> "Другое животное";
        };
    }

    private String speciesEmoji(Species species) {
        if (species == null) {
            return "🐾";
        }
        return switch (species) {
            case DOG -> "🐶";
            case CAT -> "🐱";
            case OTHER -> "🐾";
        };
    }

    private String ageLabel(Integer age) {
        if (age == null || age < 0 || age > 100) {
            return null;
        }
        return age + " " + yearWord(age);
    }

    private String yearWord(Integer age) {
        int lastTwoDigits = age % 100;
        int lastDigit = age % 10;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return "лет";
        }
        if (lastDigit == 1) {
            return "год";
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return "года";
        }
        return "лет";
    }

    private String statusLabel(AnimalStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case SEEKS_HOME -> "Ищет дом";
            case IN_FOSTER -> "На передержке";
            case QUARANTINE -> "На карантине";
            case RESERVED -> "Забронирован";
            case ADOPTED -> "Уже дома";
            case NOT_AVAILABLE -> "Не ищет дом";
        };
    }

    private String truncate(String value) {
        if (!hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= MAX_DESCRIPTION_LENGTH) {
            return trimmed;
        }
        return trimmed.substring(0, MAX_DESCRIPTION_LENGTH - 1).trim() + "…";
    }
}
