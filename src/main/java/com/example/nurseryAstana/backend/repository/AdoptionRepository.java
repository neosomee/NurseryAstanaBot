package com.example.nurseryAstana.backend.repository;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    boolean existsByAnimal_IdAndStatusIn(Long animalId, Collection<AdoptionStatus> statuses);

    boolean existsByUser_TelegramIdAndStatusIn(Long telegramId, Collection<AdoptionStatus> statuses);

    @EntityGraph(attributePaths = {"animal", "user"})
    Optional<Adoption> findFirstByUser_TelegramIdAndStatusInOrderByStartDateDesc(
            Long telegramId,
            Collection<AdoptionStatus> statuses
    );
}
