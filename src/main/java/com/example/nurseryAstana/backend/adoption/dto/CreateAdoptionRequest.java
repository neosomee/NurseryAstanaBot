package com.example.nurseryAstana.backend.adoption.dto;

import lombok.Data;

@Data
public class CreateAdoptionRequest {
    Long animalId;
    Long userId;
}
