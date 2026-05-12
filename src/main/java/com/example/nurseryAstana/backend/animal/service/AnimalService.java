package com.example.nurseryAstana.backend.animal.service;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;

import java.util.List;
import java.util.Optional;

public interface AnimalService {
    public List<AnimalResponse> findAllAnimal();
    public Optional<AnimalResponse> findAnimalById (Long id);
    public AnimalResponse createAnimal(CreateAnimalRequest animalDto);
    public AnimalResponse updateAnimal(CreateAnimalRequest animalDto);
    public void removeAnimalById(Long animalId);
    public void removeAllAnimals();
}
