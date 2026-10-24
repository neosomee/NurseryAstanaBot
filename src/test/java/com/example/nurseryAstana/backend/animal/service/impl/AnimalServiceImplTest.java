package com.example.nurseryAstana.backend.animal.service.impl;

import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.adoption.repository.AdoptionRepository;
import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.exception.AnimalHasActiveAdoptionException;
import com.example.nurseryAstana.backend.animal.exception.AnimalInvalidDataException;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotFoundException;
import com.example.nurseryAstana.backend.animal.mapper.AnimalMapping;
import com.example.nurseryAstana.backend.animal.mapper.AnimalMappingImpl;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.animal.model.Species;
import com.example.nurseryAstana.backend.animal.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalServiceImplTest {

    private static final EnumSet<AdoptionStatus> ACTIVE_TRIAL_STATUSES =
            EnumSet.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDS);

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AdoptionRepository adoptionRepository;

    @Spy
    private AnimalMapping animalMapping = new AnimalMappingImpl();

    @InjectMocks
    private AnimalServiceImpl animalService;

    @Test
    void createAnimalSavesMappedAnimal() {
        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> {
            Animal toSave = inv.getArgument(0);
            toSave.setId(7L);
            return toSave;
        });

        AnimalResponse response = animalService.createAnimal(validRequest());

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalRepository).save(captor.capture());
        Animal saved = captor.getValue();
        assertThat(saved.getSpecies()).isEqualTo(Species.DOG);
        assertThat(saved.getName()).isEqualTo("Бобик");
        assertThat(saved.getAge()).isEqualTo(3);
        assertThat(saved.getStatus()).isEqualTo(AnimalStatus.SEEKS_HOME);

        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getName()).isEqualTo("Бобик");
    }

    static Stream<Arguments> invalidRequests() {
        CreateAnimalRequest noSpecies = validRequest();
        noSpecies.setSpecies(null);
        CreateAnimalRequest noName = validRequest();
        noName.setName(null);
        CreateAnimalRequest blankName = validRequest();
        blankName.setName("   ");
        CreateAnimalRequest noAge = validRequest();
        noAge.setAge(null);
        CreateAnimalRequest negativeAge = validRequest();
        negativeAge.setAge(-1);
        return Stream.of(
                Arguments.of("пустое тело", null),
                Arguments.of("нет species", noSpecies),
                Arguments.of("нет имени", noName),
                Arguments.of("пустое имя", blankName),
                Arguments.of("нет возраста", noAge),
                Arguments.of("отрицательный возраст", negativeAge)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequests")
    void createAnimalRejectsInvalidRequest(String caseName, CreateAnimalRequest request) {
        assertThatThrownBy(() -> animalService.createAnimal(request))
                .isInstanceOf(AnimalInvalidDataException.class);
        verifyNoInteractions(animalRepository);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequests")
    void updateAnimalRejectsInvalidRequest(String caseName, CreateAnimalRequest request) {
        assertThatThrownBy(() -> animalService.updateAnimal(request))
                .isInstanceOf(AnimalInvalidDataException.class);
        verifyNoInteractions(animalRepository);
    }

    @Test
    void findAnimalByIdMapsToResponse() {
        when(animalRepository.findById(7L)).thenReturn(Optional.of(animal(7L)));

        assertThat(animalService.findAnimalById(7L))
                .hasValueSatisfying(r -> assertThat(r.getId()).isEqualTo(7L));
    }

    @Test
    void findAnimalByIdReturnsEmptyWhenMissing() {
        when(animalRepository.findById(7L)).thenReturn(Optional.empty());

        assertThat(animalService.findAnimalById(7L)).isEmpty();
    }

    @Test
    void findAllAnimalMapsEveryAnimal() {
        when(animalRepository.findAll()).thenReturn(List.of(animal(7L), animal(8L)));

        assertThat(animalService.findAllAnimal())
                .extracting(AnimalResponse::getId)
                .containsExactly(7L, 8L);
    }

    @Test
    void removeAnimalByIdDeletesAnimalWithoutActiveAdoption() {
        when(animalRepository.existsById(7L)).thenReturn(true);
        when(adoptionRepository.existsByAnimal_IdAndStatusIn(7L, ACTIVE_TRIAL_STATUSES)).thenReturn(false);

        animalService.removeAnimalById(7L);

        verify(animalRepository).deleteById(7L);
    }

    @Test
    void removeAnimalByIdThrowsWhenAnimalHasActiveAdoption() {
        when(animalRepository.existsById(7L)).thenReturn(true);
        when(adoptionRepository.existsByAnimal_IdAndStatusIn(7L, ACTIVE_TRIAL_STATUSES)).thenReturn(true);

        assertThatThrownBy(() -> animalService.removeAnimalById(7L))
                .isInstanceOf(AnimalHasActiveAdoptionException.class);
        verify(animalRepository, never()).deleteById(anyLong());
    }

    @Test
    void removeAnimalByIdThrowsWhenAnimalNotFound() {
        when(animalRepository.existsById(7L)).thenReturn(false);

        assertThatThrownBy(() -> animalService.removeAnimalById(7L))
                .isInstanceOf(AnimalNotFoundException.class);
        verify(animalRepository, never()).deleteById(anyLong());
        verifyNoInteractions(adoptionRepository);
    }

    private static CreateAnimalRequest validRequest() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setSpecies(Species.DOG);
        request.setName("Бобик");
        request.setAge(3);
        request.setBreed("Дворняга");
        request.setStatus(AnimalStatus.SEEKS_HOME);
        return request;
    }

    private static Animal animal(Long id) {
        return Animal.builder()
                .id(id)
                .species(Species.CAT)
                .name("Мурка")
                .age(2)
                .status(AnimalStatus.SEEKS_HOME)
                .build();
    }
}
