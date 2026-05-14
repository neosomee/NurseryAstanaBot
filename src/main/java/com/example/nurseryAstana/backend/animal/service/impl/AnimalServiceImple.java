package com.example.nurseryAstana.backend.animal.service.impl;

import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.exception.AnimalHasActiveAdoptionException;
import com.example.nurseryAstana.backend.animal.exception.AnimalInvalidDataException;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotFoundException;
import com.example.nurseryAstana.backend.animal.mapper.AnimalMapping;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.service.AnimalService;
import com.example.nurseryAstana.backend.repository.AdoptionRepository;
import com.example.nurseryAstana.backend.repository.AnimalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnimalServiceImple implements AnimalService {

    private static final EnumSet<AdoptionStatus> ACTIVE_TRIAL_STATUSES =
            EnumSet.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDS);

    private final AnimalRepository animalRepository;
    private final AdoptionRepository adoptionRepository;
    private final AnimalMapping animalMapping;

    public List<AnimalResponse> findAllAnimal() {
        return animalRepository.findAll()
                .stream()
                .map(animalMapping::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<AnimalResponse> findAnimalById (Long id) {
        return animalRepository.findById(id)
                .map(animalMapping::toResponse);
    }

    public AnimalResponse createAnimal(CreateAnimalRequest animalDto) {
        validateCreateAnimal(animalDto);
        Animal animal = animalMapping.toAnimal(animalDto);
        Animal savedAnimal = animalRepository.save(animal);
        return animalMapping.toResponse(savedAnimal);
    }

    public AnimalResponse updateAnimal(CreateAnimalRequest animalDto) {
        validateCreateAnimal(animalDto);
        Animal animal = animalMapping.toAnimal(animalDto);
        Animal savedAnimal = animalRepository.save(animal);
        return animalMapping.toResponse(savedAnimal);
    }

    public void removeAnimalById(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new AnimalNotFoundException(animalId);
        }
        if (adoptionRepository.existsByAnimal_IdAndStatusIn(animalId, ACTIVE_TRIAL_STATUSES)) {
            throw new AnimalHasActiveAdoptionException(animalId);
        }
        animalRepository.deleteById(animalId);
    }

    // Тестовый метод для быстрой проверки
    public void removeAllAnimals() {
        animalRepository.deleteAll();
    }

    private static void validateCreateAnimal(CreateAnimalRequest dto) {
        if (dto == null) {
            throw new AnimalInvalidDataException("Тело запроса пустое");
        }
        if (dto.getSpecies() == null) {
            throw new AnimalInvalidDataException("Поле species обязательно");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new AnimalInvalidDataException("Имя животного обязательно");
        }
        if (dto.getAge() == null || dto.getAge() < 0) {
            throw new AnimalInvalidDataException("Возраст должен быть неотрицательным числом");
        }
    }


}
