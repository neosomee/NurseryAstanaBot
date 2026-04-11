package com.example.nurseryAstana.model;

import com.example.nurseryAstana.model.enums.AnimalStatus;
import com.example.nurseryAstana.model.enums.Species;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Species species;
    private String name;
    private int age;
    private String breed;
    private String description;

    @Enumerated(EnumType.STRING)
    private AnimalStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
