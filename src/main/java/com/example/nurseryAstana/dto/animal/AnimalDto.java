package com.example.nurseryAstana.dto.animal;

import com.example.nurseryAstana.model.enums.AnimalStatus;
import com.example.nurseryAstana.model.enums.Species;
import lombok.Data;

@Data
public class AnimalDto {
    private Species species;
    private String name;
    private int age;
    private String breed;
    private String description;
    private AnimalStatus status;
}
