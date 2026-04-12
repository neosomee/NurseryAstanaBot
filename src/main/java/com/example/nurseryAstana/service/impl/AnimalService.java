package com.example.nurseryAstana.service.impl;

import com.example.nurseryAstana.dto.AnimalDto;
import com.example.nurseryAstana.dto.mapper.AnimalMapping;
import com.example.nurseryAstana.model.Animal;
import com.example.nurseryAstana.repository.AnimalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalMapping animalMapping;

    public List<AnimalDto> findAllAnimal() {
        return animalRepository.findAll()
                .stream()
                .map(animalMapping::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<AnimalDto> findAnimalById (Long id) {
        return animalRepository.findById(id)
                .map(animalMapping::toDTO);
    }

    public AnimalDto createAnimal(AnimalDto animalDto) {
        Animal animal = animalMapping.toAnimal(animalDto);
        Animal savedAnimal = animalRepository.save(animal);
        return animalMapping.toDTO(savedAnimal);
    }

    public AnimalDto updateAnimal(AnimalDto animalDto) {
        Animal animal = animalMapping.toAnimal(animalDto);
        Animal savedAnimal = animalRepository.save(animal);
        return animalMapping.toDTO(savedAnimal);
    }

    public void removeAnimalById(Long animalId) {
        animalRepository.deleteById(animalId);
    }

    // Тестовый метод для быстрой проверки
    public void removeAllAnimals() {
        animalRepository.deleteAll();
    }


}
