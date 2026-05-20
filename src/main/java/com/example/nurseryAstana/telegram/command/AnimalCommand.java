package com.example.nurseryAstana.telegram.command;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.Species;
import com.example.nurseryAstana.telegram.service.TelegramAnimalAdoptionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnimalCommand implements BotCommand {

    public static final String TAKE_ANIMAL_CALLBACK = "TAKE_ANIMAL";
    public static final String DECLINE_ANIMAL_CALLBACK = "DECLINE_ANIMAL";

    private static final int MAX_DESCRIPTION_LENGTH = 160;

    private final TelegramBot telegramBot;
    private final TelegramAnimalAdoptionService animalAdoptionService;

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
        Long telegramId = message != null && message.from() != null ? message.from().id() : chatId;
        Optional<Adoption> currentAdoption = animalAdoptionService.findCurrentAdoption(telegramId);

        currentAdoption.ifPresent(adoption ->
                telegramBot.execute(new SendMessage(chatId, formatCurrentAnimalMessage(adoption)))
        );

        List<Animal> availableAnimals = animalAdoptionService.findAvailableAnimals();
        if (availableAnimals.isEmpty()) {
            telegramBot.execute(new SendMessage(chatId, "Пока нет животных для усыновления"));
            return;
        }

        for (Animal animal : availableAnimals) {
            telegramBot.execute(new SendMessage(chatId, formatAvailableAnimalMessage(animal))
                    .replyMarkup(animalActionsKeyboard(animal.getId())));
        }
    }

    private InlineKeyboardMarkup animalActionsKeyboard(Long animalId) {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Взять").callbackData(takeCallback(animalId)),
                new InlineKeyboardButton("Отказаться").callbackData(declineCallback(animalId))
        );
    }

    public static String takeCallback(Long animalId) {
        return TAKE_ANIMAL_CALLBACK + ":" + animalId;
    }

    public static String declineCallback(Long animalId) {
        return DECLINE_ANIMAL_CALLBACK + ":" + animalId;
    }

    public static boolean isTakeCallback(String data) {
        return data != null && data.startsWith(TAKE_ANIMAL_CALLBACK + ":");
    }

    public static boolean isDeclineCallback(String data) {
        return data != null && data.startsWith(DECLINE_ANIMAL_CALLBACK + ":");
    }

    public static Long animalIdFromCallback(String data) {
        if (data == null || !data.contains(":")) {
            throw new IllegalArgumentException("Некорректные данные кнопки");
        }
        return Long.valueOf(data.substring(data.indexOf(':') + 1));
    }

    private String formatCurrentAnimalMessage(Adoption adoption) {
        StringBuilder text = new StringBuilder("Ваше текущее животное:");
        Animal animal = adoption.getAnimal();
        appendLine(text, "Имя", animal != null ? animal.getName() : null);
        appendLine(text, "Вид", animal != null ? speciesLabel(animal.getSpecies()) : null);
        appendLine(text, "Возраст", animal != null ? ageLabel(animal.getAge()) : null);
        appendLine(text, "Статус", adoptionStatusLabel(adoption.getStatus()));
        return text.toString();
    }

    private String formatAvailableAnimalMessage(Animal animal) {
        StringBuilder text = new StringBuilder();
        appendLine(text, "Имя", animal.getName());
        appendLine(text, "Вид", speciesLabel(animal.getSpecies()));
        appendLine(text, "Возраст", ageLabel(animal.getAge()));
        appendLine(text, "Описание", truncate(animal.getDescription()));
        return text.toString().trim();
    }

    private void appendLine(StringBuilder text, String label, String value) {
        if (hasText(value)) {
            if (!text.isEmpty()) {
                text.append("\n");
            }
            text.append(label)
                    .append(": ")
                    .append(value.trim());
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String speciesLabel(Species species) {
        if (species == null) {
            return null;
        }
        return switch (species) {
            case DOG -> "Собака";
            case CAT -> "Кошка";
            case OTHER -> "Другое животное";
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

    private String adoptionStatusLabel(AdoptionStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case TRIAL -> "Испытательный срок";
            case EXTENDS -> "Испытательный срок продлен";
            case SUCCESS -> "Усыновление завершено";
            case FAILED -> "Усыновление не состоялось";
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
