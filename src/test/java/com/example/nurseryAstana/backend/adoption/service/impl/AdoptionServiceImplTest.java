package com.example.nurseryAstana.backend.adoption.service.impl;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionActiveTrialExistsException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionEndDateMissingException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionInvalidRequestException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionInvalidStatusException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionNotFoundException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionTrialExtensionInvalidException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionUserNotFoundException;
import com.example.nurseryAstana.backend.adoption.mapper.AdoptionDtoMapping;
import com.example.nurseryAstana.backend.adoption.mapper.AdoptionDtoMappingImpl;
import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.adoption.repository.AdoptionRepository;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotAvailableForAdoptionException;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotFoundException;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.animal.repository.AnimalRepository;
import com.example.nurseryAstana.backend.user.model.User;
import com.example.nurseryAstana.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceImplTest {

    private static final EnumSet<AdoptionStatus> ACTIVE_TRIAL_STATUSES =
            EnumSet.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDS);

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private AdoptionDtoMapping adoptionDtoMapping = new AdoptionDtoMappingImpl();

    @InjectMocks
    private AdoptionServiceImpl adoptionService;

    // --- createAdoption ---

    @Test
    void createAdoptionSavesTrialAdoptionForAvailableAnimal() {
        Animal animal = animal(1L, AnimalStatus.SEEKS_HOME);
        User user = user(2L);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(adoptionRepository.existsByAnimal_IdAndStatusIn(1L, ACTIVE_TRIAL_STATUSES)).thenReturn(false);
        when(adoptionRepository.save(any(Adoption.class))).thenAnswer(inv -> {
            Adoption toSave = inv.getArgument(0);
            toSave.setId(10L);
            return toSave;
        });

        LocalDateTime before = LocalDateTime.now();
        AdoptionResponse response = adoptionService.createAdoption(request(1L, 2L));

        ArgumentCaptor<Adoption> captor = ArgumentCaptor.forClass(Adoption.class);
        verify(adoptionRepository).save(captor.capture());
        Adoption saved = captor.getValue();
        assertThat(saved.getAnimal()).isSameAs(animal);
        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getStatus()).isEqualTo(AdoptionStatus.TRIAL);
        assertThat(saved.getStartDate()).isAfterOrEqualTo(before);
        assertThat(saved.getEndDate()).isEqualToIgnoringNanos(saved.getStartDate().plusDays(30));

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getAnimalId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(2L);
        assertThat(response.getStatus()).isEqualTo(AdoptionStatus.TRIAL);
    }

    @Test
    void createAdoptionRejectsSecondActiveTrialForSameAnimal() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal(1L, AnimalStatus.SEEKS_HOME)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
        when(adoptionRepository.existsByAnimal_IdAndStatusIn(1L, ACTIVE_TRIAL_STATUSES)).thenReturn(true);

        assertThatThrownBy(() -> adoptionService.createAdoption(request(1L, 2L)))
                .isInstanceOf(AdoptionActiveTrialExistsException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    void createAdoptionThrowsWhenAnimalNotFound() {
        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adoptionService.createAdoption(request(1L, 2L)))
                .isInstanceOf(AnimalNotFoundException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    void createAdoptionThrowsWhenUserNotFound() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal(1L, AnimalStatus.SEEKS_HOME)));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adoptionService.createAdoption(request(1L, 2L)))
                .isInstanceOf(AdoptionUserNotFoundException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = AnimalStatus.class, names = "SEEKS_HOME", mode = EnumSource.Mode.EXCLUDE)
    void createAdoptionThrowsWhenAnimalIsNotSeekingHome(AnimalStatus status) {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(animal(1L, status)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));

        assertThatThrownBy(() -> adoptionService.createAdoption(request(1L, 2L)))
                .isInstanceOf(AnimalNotAvailableForAdoptionException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    void createAdoptionThrowsWhenRequestIsNull() {
        assertThatThrownBy(() -> adoptionService.createAdoption(null))
                .isInstanceOf(AdoptionInvalidRequestException.class);
        verifyNoInteractions(adoptionRepository, animalRepository, userRepository);
    }

    @Test
    void createAdoptionThrowsWhenIdsAreMissing() {
        assertThatThrownBy(() -> adoptionService.createAdoption(request(null, 2L)))
                .isInstanceOf(AdoptionInvalidRequestException.class);
        assertThatThrownBy(() -> adoptionService.createAdoption(request(1L, null)))
                .isInstanceOf(AdoptionInvalidRequestException.class);
        verifyNoInteractions(adoptionRepository, animalRepository, userRepository);
    }

    // --- extendTrial ---

    @ParameterizedTest
    @EnumSource(value = AdoptionStatus.class, names = {"TRIAL", "EXTENDS"})
    void extendTrialMovesEndDateAndSetsExtendsStatus(AdoptionStatus status) {
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 31, 12, 0);
        Adoption adoption = adoption(5L, status, endDate);
        when(adoptionRepository.findById(5L)).thenReturn(Optional.of(adoption));
        when(adoptionRepository.save(adoption)).thenReturn(adoption);

        AdoptionResponse response = adoptionService.extendTrial(5L, 14);

        assertThat(response.getStatus()).isEqualTo(AdoptionStatus.EXTENDS);
        assertThat(response.getEndDate()).isEqualTo(endDate.plusDays(14));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -30})
    void extendTrialRejectsNonPositiveDays(int days) {
        assertThatThrownBy(() -> adoptionService.extendTrial(5L, days))
                .isInstanceOf(AdoptionTrialExtensionInvalidException.class);
        verifyNoInteractions(adoptionRepository);
    }

    @Test
    void extendTrialThrowsWhenEndDateMissing() {
        when(adoptionRepository.findById(5L)).thenReturn(Optional.of(adoption(5L, AdoptionStatus.TRIAL, null)));

        assertThatThrownBy(() -> adoptionService.extendTrial(5L, 14))
                .isInstanceOf(AdoptionEndDateMissingException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = AdoptionStatus.class, names = {"SUCCESS", "FAILED"})
    void extendTrialRejectsFinishedAdoption(AdoptionStatus status) {
        when(adoptionRepository.findById(5L))
                .thenReturn(Optional.of(adoption(5L, status, LocalDateTime.now())));

        assertThatThrownBy(() -> adoptionService.extendTrial(5L, 14))
                .isInstanceOf(AdoptionInvalidStatusException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    void extendTrialThrowsWhenAdoptionNotFound() {
        when(adoptionRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adoptionService.extendTrial(5L, 14))
                .isInstanceOf(AdoptionNotFoundException.class);
    }

    // --- finishTrial / failTrial ---

    @ParameterizedTest
    @EnumSource(value = AdoptionStatus.class, names = {"TRIAL", "EXTENDS"})
    void finishTrialSetsSuccessStatus(AdoptionStatus status) {
        Adoption adoption = adoption(5L, status, LocalDateTime.now());
        when(adoptionRepository.findById(5L)).thenReturn(Optional.of(adoption));
        when(adoptionRepository.save(adoption)).thenReturn(adoption);

        assertThat(adoptionService.finishTrial(5L).getStatus()).isEqualTo(AdoptionStatus.SUCCESS);
    }

    @ParameterizedTest
    @EnumSource(value = AdoptionStatus.class, names = {"TRIAL", "EXTENDS"})
    void failTrialSetsFailedStatus(AdoptionStatus status) {
        Adoption adoption = adoption(5L, status, LocalDateTime.now());
        when(adoptionRepository.findById(5L)).thenReturn(Optional.of(adoption));
        when(adoptionRepository.save(adoption)).thenReturn(adoption);

        assertThat(adoptionService.failTrial(5L).getStatus()).isEqualTo(AdoptionStatus.FAILED);
    }

    @ParameterizedTest
    @EnumSource(value = AdoptionStatus.class, names = {"SUCCESS", "FAILED"})
    void finishAndFailTrialRejectFinishedAdoption(AdoptionStatus status) {
        when(adoptionRepository.findById(5L))
                .thenReturn(Optional.of(adoption(5L, status, LocalDateTime.now())));

        assertThatThrownBy(() -> adoptionService.finishTrial(5L))
                .isInstanceOf(AdoptionInvalidStatusException.class);
        assertThatThrownBy(() -> adoptionService.failTrial(5L))
                .isInstanceOf(AdoptionInvalidStatusException.class);
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    void finishAndFailTrialThrowWhenAdoptionNotFound() {
        when(adoptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adoptionService.finishTrial(5L))
                .isInstanceOf(AdoptionNotFoundException.class);
        assertThatThrownBy(() -> adoptionService.failTrial(5L))
                .isInstanceOf(AdoptionNotFoundException.class);
    }

    // --- find / remove ---

    @Test
    void findAdoptionByIdMapsToResponse() {
        when(adoptionRepository.findById(5L))
                .thenReturn(Optional.of(adoption(5L, AdoptionStatus.TRIAL, LocalDateTime.now())));

        Optional<AdoptionResponse> response = adoptionService.findAdoptionById(5L);

        assertThat(response).hasValueSatisfying(r -> {
            assertThat(r.getId()).isEqualTo(5L);
            assertThat(r.getAnimalId()).isEqualTo(1L);
            assertThat(r.getUserId()).isEqualTo(2L);
        });
    }

    @Test
    void findAdoptionByIdReturnsEmptyWhenMissing() {
        when(adoptionRepository.findById(5L)).thenReturn(Optional.empty());

        assertThat(adoptionService.findAdoptionById(5L)).isEmpty();
    }

    @Test
    void findAllAdoptionsMapsEveryAdoption() {
        when(adoptionRepository.findAll()).thenReturn(List.of(
                adoption(5L, AdoptionStatus.TRIAL, LocalDateTime.now()),
                adoption(6L, AdoptionStatus.SUCCESS, LocalDateTime.now())
        ));

        assertThat(adoptionService.findAllAdoptions())
                .extracting(AdoptionResponse::getId)
                .containsExactly(5L, 6L);
    }

    @Test
    void removeAdoptionByIdDeletesExistingAdoption() {
        when(adoptionRepository.existsById(5L)).thenReturn(true);

        adoptionService.removeAdoptionById(5L);

        verify(adoptionRepository).deleteById(5L);
    }

    @Test
    void removeAdoptionByIdThrowsWhenMissing() {
        when(adoptionRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> adoptionService.removeAdoptionById(5L))
                .isInstanceOf(AdoptionNotFoundException.class);
        verify(adoptionRepository, never()).deleteById(eq(5L));
    }

    private static CreateAdoptionRequest request(Long animalId, Long userId) {
        CreateAdoptionRequest request = new CreateAdoptionRequest();
        request.setAnimalId(animalId);
        request.setUserId(userId);
        return request;
    }

    private static Animal animal(Long id, AnimalStatus status) {
        return Animal.builder().id(id).name("Бобик").status(status).build();
    }

    private static User user(Long id) {
        return User.builder().id(id).telegramId(100L + id).username("user" + id).build();
    }

    private static Adoption adoption(Long id, AdoptionStatus status, LocalDateTime endDate) {
        return Adoption.builder()
                .id(id)
                .animal(animal(1L, AnimalStatus.IN_FOSTER))
                .user(user(2L))
                .startDate(endDate == null ? LocalDateTime.now() : endDate.minusDays(30))
                .endDate(endDate)
                .status(status)
                .build();
    }
}
