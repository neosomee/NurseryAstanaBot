package com.example.nurseryAstana.service;

import com.example.nurseryAstana.model.Animal;
import com.example.nurseryAstana.repository.AnimalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public List<Animal> findAllAnimal() {
        return animalRepository.findAll();
    }

    public Animal findAnimalById (Long id) {
        return animalRepository.findById(id).orElse(null);
    }

    public Animal createAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal updateAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public void removeAnimalById(Long animalId) {
        animalRepository.deleteById(animalId);
    }

    // Тестовый метод для быстрой проверки
    public void removeAllAnimals() {
        animalRepository.deleteAll();
    }


}
