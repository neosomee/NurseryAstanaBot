package com.example.nurseryAstana.telegram.command;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.animal.model.Species;
import com.example.nurseryAstana.telegram.service.TelegramAnimalAdoptionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AnimalCommandTest {

    private final TelegramBot telegramBot = mock(TelegramBot.class);
    private final TelegramAnimalAdoptionService animalAdoptionService = mock(TelegramAnimalAdoptionService.class);
    private final AnimalCommand command = new AnimalCommand(telegramBot, animalAdoptionService);

    @Test
    void getCommandReturnsAnimalCommand() {
        assertThat(command.getCommand()).isEqualTo("/animal");
    }

    @Test
    void supportsOnlyAnimalCommand() {
        assertThat(command.supports("/animal")).isTrue();
        assertThat(command.supports("/start")).isFalse();
    }

    @Test
    void executeSendsCurrentAnimalWithoutButtonsThenAvailableAnimalsSeparately() {
        Adoption adoption = new Adoption();
        adoption.setAnimal(animal(1L, "Бим", AnimalStatus.RESERVED));
        adoption.setStatus(AdoptionStatus.TRIAL);
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.of(adoption));
        when(animalAdoptionService.findAvailableAnimals()).thenReturn(List.of(
                animal(2L, "Боня", AnimalStatus.SEEKS_HOME),
                animal(3L, "Снежок", AnimalStatus.SEEKS_HOME)
        ));

        command.execute(123L, messageFrom(777L));

        List<SendMessage> requests = captureSendMessages(3);
        assertThat(requests.get(0).getParameters().get("text").toString())
                .contains("Ваше текущее животное:")
                .contains("Имя: Бим")
                .contains("Статус: Испытательный срок");
        assertThat(requests.get(0).getParameters()).doesNotContainKey("reply_markup");

        assertThat(requests.get(1).getParameters().get("text").toString())
                .contains("Имя: Боня")
                .contains("Вид: Собака")
                .contains("Возраст: 3 года")
                .contains("Описание: Ласковый и спокойный пес")
                .doesNotContain("Animal{id=")
                .doesNotContain("SEEKS_HOME");
        assertAnimalButtons(requests.get(1), 2L);

        assertThat(requests.get(2).getParameters().get("text").toString()).contains("Имя: Снежок");
        assertAnimalButtons(requests.get(2), 3L);

        verify(animalAdoptionService).findCurrentAdoption(777L);
        verify(animalAdoptionService).findAvailableAnimals();
        verifyNoMoreInteractions(animalAdoptionService, telegramBot);
    }

    @Test
    void executeSendsEachAvailableAnimalAsSeparateMessage() {
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.empty());
        when(animalAdoptionService.findAvailableAnimals()).thenReturn(List.of(
                animal(2L, "Боня", AnimalStatus.SEEKS_HOME),
                animal(3L, "Снежок", AnimalStatus.SEEKS_HOME)
        ));

        command.execute(123L, messageFrom(777L));

        List<SendMessage> requests = captureSendMessages(2);
        assertThat(requests.get(0).getParameters().get("text").toString()).contains("Имя: Боня");
        assertAnimalButtons(requests.get(0), 2L);
        assertThat(requests.get(1).getParameters().get("text").toString()).contains("Имя: Снежок");
        assertAnimalButtons(requests.get(1), 3L);

        verify(animalAdoptionService).findCurrentAdoption(777L);
        verify(animalAdoptionService).findAvailableAnimals();
        verifyNoMoreInteractions(animalAdoptionService, telegramBot);
    }

    @Test
    void executeSendsFallbackWhenNoAvailableAnimals() {
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.empty());
        when(animalAdoptionService.findAvailableAnimals()).thenReturn(List.of());

        command.execute(123L, messageFrom(777L));

        SendMessage request = captureSendMessages(1).get(0);
        assertThat(request.getParameters().get("text")).isEqualTo("Пока нет животных для усыновления");
        assertThat(request.getParameters()).doesNotContainKey("reply_markup");

        verify(animalAdoptionService).findCurrentAdoption(777L);
        verify(animalAdoptionService).findAvailableAnimals();
        verifyNoMoreInteractions(animalAdoptionService, telegramBot);
    }

    @Test
    void executeTruncatesLongDescriptions() {
        Animal animal = animal(2L, "Боня", AnimalStatus.SEEKS_HOME);
        animal.setDescription("Очень ".repeat(60));
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.empty());
        when(animalAdoptionService.findAvailableAnimals()).thenReturn(List.of(animal));

        command.execute(123L, messageFrom(777L));

        String text = captureSendMessages(1).get(0).getParameters().get("text").toString();
        assertThat(text).contains("Описание:");
        assertThat(text).contains("…");
        assertThat(text.length()).isLessThan(250);

        verify(animalAdoptionService).findCurrentAdoption(777L);
        verify(animalAdoptionService).findAvailableAnimals();
        verifyNoMoreInteractions(animalAdoptionService, telegramBot);
    }

    private Message messageFrom(Long telegramId) {
        Message message = mock(Message.class);
        when(message.from()).thenReturn(new User(telegramId));
        return message;
    }

    private List<SendMessage> captureSendMessages(int count) {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, org.mockito.Mockito.times(count)).execute(captor.capture());
        return captor.getAllValues();
    }

    private void assertAnimalButtons(SendMessage request, Long animalId) {
        InlineKeyboardMarkup markup = (InlineKeyboardMarkup) request.getParameters().get("reply_markup");
        assertThat(markup).isNotNull();

        InlineKeyboardButton[][] buttons = markup.inlineKeyboard();
        assertThat(buttons).hasDimensions(1, 2);
        assertThat(buttons[0][0].text()).isEqualTo("Взять");
        assertThat(buttons[0][0].callbackData()).isEqualTo(AnimalCommand.takeCallback(animalId));
        assertThat(buttons[0][1].text()).isEqualTo("Отказаться");
        assertThat(buttons[0][1].callbackData()).isEqualTo(AnimalCommand.declineCallback(animalId));
    }

    private Animal animal(Long id, String name, AnimalStatus status) {
        Animal animal = new Animal();
        animal.setId(id);
        animal.setSpecies(Species.DOG);
        animal.setName(name);
        animal.setAge(3);
        animal.setBreed("метис");
        animal.setDescription("Ласковый и спокойный пес");
        animal.setStatus(status);
        return animal;
    }
}
