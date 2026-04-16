package com.example.nurseryAstana.dto.mapper;

import com.example.nurseryAstana.dto.animal.AnimalResponse;
import com.example.nurseryAstana.dto.animal.CreateAnimalRequest;
import com.example.nurseryAstana.model.Animal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalMapping {
    AnimalResponse toResponse(Animal animal);
    Animal toAnimal(CreateAnimalRequest animalDto);
}
