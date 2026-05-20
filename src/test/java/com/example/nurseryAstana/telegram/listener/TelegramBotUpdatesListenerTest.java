package com.example.nurseryAstana.telegram.listener;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.telegram.command.AnimalCommand;
import com.example.nurseryAstana.telegram.command.ReportFlowHandler;
import com.example.nurseryAstana.telegram.command.ReportStateManager;
import com.example.nurseryAstana.telegram.service.TelegramAnimalAdoptionService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TelegramBotUpdatesListenerTest {

    private final TelegramBot telegramBot = mock(TelegramBot.class);
    private final ReportStateManager stateManager = mock(ReportStateManager.class);
    private final ReportFlowHandler reportFlowHandler = mock(ReportFlowHandler.class);
    private final TelegramAnimalAdoptionService animalAdoptionService = mock(TelegramAnimalAdoptionService.class);

    @Test
    void handleTakeAnimalCallbackCreatesAdoptionAndRemovesButtons() {
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.empty());

        UpdatesListener updatesListener = initAndCaptureUpdatesListener();
        updatesListener.process(List.of(callbackUpdate(AnimalCommand.takeCallback(2L))));

        verify(telegramBot).execute(org.mockito.ArgumentMatchers.any(EditMessageReplyMarkup.class));
        SendMessage request = captureSendMessage();
        assertThat(request.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(request.getParameters().get("text")).isEqualTo("Животное успешно закреплено за вами");
        verify(animalAdoptionService).findCurrentAdoption(777L);
        verify(animalAdoptionService).takeAnimal(777L, 2L);
        verifyNoMoreInteractions(stateManager, reportFlowHandler, animalAdoptionService);
    }

    @Test
    void handleTakeAnimalCallbackDoesNotCreateAdoptionWhenUserAlreadyHasAnimal() {
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.of(new Adoption()));

        UpdatesListener updatesListener = initAndCaptureUpdatesListener();
        updatesListener.process(List.of(callbackUpdate(AnimalCommand.takeCallback(2L))));

        SendMessage request = captureSendMessage();
        assertThat(request.getParameters().get("text")).isEqualTo("У вас уже есть закрепленное животное");
        verify(animalAdoptionService).findCurrentAdoption(777L);
        verifyNoMoreInteractions(stateManager, reportFlowHandler, animalAdoptionService);
    }

    @Test
    void handleDeclineAnimalCallbackUpdatesMessageAndRemovesButtonsWhenUserHasNoActiveAnimal() {
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.empty());

        UpdatesListener updatesListener = initAndCaptureUpdatesListener();
        updatesListener.process(List.of(callbackUpdate(AnimalCommand.declineCallback(2L))));

        verify(telegramBot).execute(org.mockito.ArgumentMatchers.any(EditMessageReplyMarkup.class));
        verify(telegramBot).execute(org.mockito.ArgumentMatchers.any(EditMessageText.class));
        SendMessage request = captureSendMessage();
        assertThat(request.getParameters().get("text")).isEqualTo("Хорошо, тогда посмотрите других животных");
        verify(animalAdoptionService).findCurrentAdoption(777L);
        verifyNoMoreInteractions(stateManager, reportFlowHandler, animalAdoptionService);
    }

    @Test
    void handleDeclineAnimalCallbackRejectsDeclineWhenUserHasActiveAnimal() {
        when(animalAdoptionService.findCurrentAdoption(777L)).thenReturn(Optional.of(new Adoption()));

        UpdatesListener updatesListener = initAndCaptureUpdatesListener();
        updatesListener.process(List.of(callbackUpdate(AnimalCommand.declineCallback(2L))));

        SendMessage request = captureSendMessage();
        assertThat(request.getParameters().get("text")).isEqualTo("После закрепления животного отказ через эту кнопку невозможен");
        verify(animalAdoptionService).findCurrentAdoption(777L);
        verifyNoMoreInteractions(stateManager, reportFlowHandler, animalAdoptionService);
    }

    private UpdatesListener initAndCaptureUpdatesListener() {
        TelegramBotUpdatesListener listener = new TelegramBotUpdatesListener(
                telegramBot,
                List.of(),
                stateManager,
                reportFlowHandler,
                animalAdoptionService
        );
        listener.init();

        ArgumentCaptor<UpdatesListener> captor = ArgumentCaptor.forClass(UpdatesListener.class);
        verify(telegramBot).setUpdatesListener(captor.capture());
        clearInvocations(telegramBot);
        return captor.getValue();
    }

    private Update callbackUpdate(String data) {
        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123L);

        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        when(message.messageId()).thenReturn(55);
        when(message.text()).thenReturn("Имя: Боня");

        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        when(callbackQuery.data()).thenReturn(data);
        when(callbackQuery.message()).thenReturn(message);
        when(callbackQuery.from()).thenReturn(new User(777L));

        Update update = mock(Update.class);
        when(update.callbackQuery()).thenReturn(callbackQuery);
        return update;
    }

    private SendMessage captureSendMessage() {
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());
        return captor.getValue();
    }
}
