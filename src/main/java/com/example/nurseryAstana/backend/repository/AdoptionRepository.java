package com.example.nurseryAstana.backend.repository;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
}
