package com.example.nurseryAstana.dto.adoption;

import com.example.nurseryAstana.model.enums.AdoptionStatus;
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
