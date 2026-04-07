package com.example.nurseryAstana.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Species species;
    private String name;
    private int age;
    private String breed;
    private String description;


    //пока не понял какой может быть статус для животного.
    //private String status;

    //Ещё не создан
    //private ShelterId;


}
