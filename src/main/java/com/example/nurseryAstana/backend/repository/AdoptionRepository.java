package com.example.nurseryAstana.backend.repository;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    boolean existsByAnimal_IdAndStatusIn(Long animalId, Collection<AdoptionStatus> statuses);
}
