package com.example.nurseryAstana.dto.mapper;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import com.example.nurseryAstana.model.Adoption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdoptionMapping {
    @Mapping(target = "animalId", expression = "java(adoption.getAnimal().getId())")
    @Mapping(target = "userId", expression = "java(adoption.getUser().getId())")
    AdoptionResponse toResponse(Adoption adoption);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "animal", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "startTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "endTime", expression = "java(java.time.LocalDateTime.now().plusDays(request.getTrialDays()))")
    @Mapping(target = "status", constant = "TRIAL")
    Adoption toAdoption(AdoptionResponse adoptionResponse);
}
