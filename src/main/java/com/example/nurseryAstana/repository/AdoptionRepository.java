package com.example.nurseryAstana.repository;

import com.example.nurseryAstana.model.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
}
