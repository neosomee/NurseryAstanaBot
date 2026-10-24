package com.example.nurseryAstana.backend.adoption.mapper;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.model.Adoption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdoptionDtoMapping {
    @Mapping(target = "animalId", expression = "java(adoption.getAnimal().getId())")
    @Mapping(target = "userId", expression = "java(adoption.getUser().getId())")
    AdoptionResponse toResponse(Adoption adoption);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "startDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endDate", expression = "java(java.time.LocalDateTime.now().plusDays(30))")
    @Mapping(target = "status", constant = "TRIAL")
    Adoption toAdoption(CreateAdoptionRequest request);
}
