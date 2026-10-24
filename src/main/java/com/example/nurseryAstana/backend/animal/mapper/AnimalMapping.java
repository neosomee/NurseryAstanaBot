package com.example.nurseryAstana.backend.animal.mapper;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.model.Animal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalMapping {
    AnimalResponse toResponse(Animal animal);
    Animal toAnimal(CreateAnimalRequest animalDto);
}
