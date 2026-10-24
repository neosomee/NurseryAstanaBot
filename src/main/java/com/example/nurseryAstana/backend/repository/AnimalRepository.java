package com.example.nurseryAstana.backend.repository;

import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findFirstByStatusOrderByCreatedAtAsc(AnimalStatus status);

    List<Animal> findAllByStatusOrderByCreatedAtAsc(AnimalStatus status);
}
