package com.example.nurseryAstana.backend.animal.service.impl;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.mapper.AnimalMapping;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.repository.AnimalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnimalServiceImple {

    private final AnimalRepository animalRepository;
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
        Animal animal = animalMapping.toAnimal(animalDto);
        Animal savedAnimal = animalRepository.save(animal);
        return animalMapping.toResponse(savedAnimal);
    }

    public AnimalResponse updateAnimal(CreateAnimalRequest animalDto) {
        Animal animal = animalMapping.toAnimal(animalDto);
        Animal savedAnimal = animalRepository.save(animal);
        return animalMapping.toResponse(savedAnimal);
    }

    public void removeAnimalById(Long animalId) {
        animalRepository.deleteById(animalId);
    }

    // Тестовый метод для быстрой проверки
    public void removeAllAnimals() {
        animalRepository.deleteAll();
    }


}
