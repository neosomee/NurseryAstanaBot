package com.example.nurseryAstana.repository;

import com.example.nurseryAstana.backend.animal.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
