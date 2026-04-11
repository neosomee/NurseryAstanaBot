package com.example.nurseryAstana.dto.mapper;

import com.example.nurseryAstana.dto.AnimalDto;
import com.example.nurseryAstana.model.Animal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalMapping {
    AnimalDto toDTO(Animal animal);
}
