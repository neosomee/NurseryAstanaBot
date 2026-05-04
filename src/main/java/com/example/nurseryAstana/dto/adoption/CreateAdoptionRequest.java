package com.example.nurseryAstana.dto.adoption;

import lombok.Data;

@Data
public class CreateAdoptionRequest {
    Long animalId;
    Long userId;
}
