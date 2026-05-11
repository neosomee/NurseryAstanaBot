package com.example.nurseryAstana.dto.animal;

import com.example.nurseryAstana.model.enums.AnimalStatus;
import com.example.nurseryAstana.model.enums.Species;
import lombok.Data;

@Data
public class AnimalResponse {
    private Long id;
    private Species species;
    private String name;
    private Integer age;
    private String breed;
    private String description;
    private AnimalStatus status;
}
