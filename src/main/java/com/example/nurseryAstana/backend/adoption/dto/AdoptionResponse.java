package com.example.nurseryAstana.backend.adoption.dto;

import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdoptionResponse {
    Long id;
    Long animalId;
    Long userId;
    LocalDateTime startDate;
    LocalDateTime endDate;
    AdoptionStatus status;
}
