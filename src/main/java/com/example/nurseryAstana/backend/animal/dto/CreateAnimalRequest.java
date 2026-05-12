package com.example.nurseryAstana.backend.animal.dto;

import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.model.enums.Species;
import lombok.Data;

@Data
public class CreateAnimalRequest {
    private Species species;
    private String name;
    private Integer age;
    private String breed;
    private String description;
    private AnimalStatus status;
}
