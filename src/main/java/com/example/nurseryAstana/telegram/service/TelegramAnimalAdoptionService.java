package com.example.nurseryAstana.telegram.service;

import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.repository.AdoptionRepository;
import com.example.nurseryAstana.backend.repository.AnimalRepository;
import com.example.nurseryAstana.backend.user.model.User;
import com.example.nurseryAstana.backend.user.service.imple.UserServiceImple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramAnimalAdoptionService {

    private static final EnumSet<AdoptionStatus> ACTIVE_STATUSES =
            EnumSet.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDS, AdoptionStatus.SUCCESS);

    private static final EnumSet<AdoptionStatus> ACTIVE_TRIAL_STATUSES =
            EnumSet.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDS);

    private final AdoptionRepository adoptionRepository;
    private final AnimalRepository animalRepository;
    private final UserServiceImple userService;

    @Transactional(readOnly = true)
    public Optional<Adoption> findCurrentAdoption(Long telegramId) {
        if (telegramId == null) {
            return Optional.empty();
        }
        return adoptionRepository.findFirstByUser_TelegramIdAndStatusInOrderByStartDateDesc(
                telegramId,
                ACTIVE_STATUSES
        );
    }

    @Transactional(readOnly = true)
    public Optional<Animal> findAvailableAnimal() {
        return animalRepository.findFirstByStatusOrderByCreatedAtAsc(AnimalStatus.SEEKS_HOME);
    }

    @Transactional(readOnly = true)
    public List<Animal> findAvailableAnimals() {
        return animalRepository.findAllByStatusOrderByCreatedAtAsc(AnimalStatus.SEEKS_HOME);
    }

    @Transactional
    public Adoption takeAnimal(Long telegramId, Long animalId) {
        if (telegramId == null || animalId == null) {
            throw new IllegalArgumentException("Не удалось определить пользователя или животное");
        }
        if (adoptionRepository.existsByUser_TelegramIdAndStatusIn(telegramId, ACTIVE_STATUSES)) {
            throw new IllegalStateException("У вас уже есть активное животное");
        }

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalStateException("Животное не найдено"));
        if (animal.getStatus() != AnimalStatus.SEEKS_HOME) {
            throw new IllegalStateException("Это животное уже недоступно для усыновления");
        }
        if (adoptionRepository.existsByAnimal_IdAndStatusIn(animalId, ACTIVE_TRIAL_STATUSES)) {
            throw new IllegalStateException("На это животное уже есть активная заявка");
        }

        User user = userService.createOrGetUser(telegramId, null);

        Adoption adoption = new Adoption();
        adoption.setAnimal(animal);
        adoption.setUser(user);
        adoption.setStartDate(LocalDateTime.now());
        adoption.setEndDate(LocalDateTime.now().plusDays(30));
        adoption.setStatus(AdoptionStatus.TRIAL);

        animal.setStatus(AnimalStatus.RESERVED);
        animalRepository.save(animal);

        return adoptionRepository.save(adoption);
    }
}
