package com.example.nurseryAstana.backend.adoption.service.imple;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionActiveTrialExistsException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionEndDateMissingException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionInvalidRequestException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionInvalidStatusException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionNotFoundException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionTrialExtensionInvalidException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionUserNotFoundException;
import com.example.nurseryAstana.backend.adoption.mapper.AdoptionDtoMapping;
import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.adoption.service.AdoptionService;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotAvailableForAdoptionException;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotFoundException;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.user.model.User;
import com.example.nurseryAstana.backend.repository.AdoptionRepository;
import com.example.nurseryAstana.backend.repository.AnimalRepository;
import com.example.nurseryAstana.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    private final AdoptionDtoMapping adoptionDtoMapping;

    private static final EnumSet<AdoptionStatus> ACTIVE_TRIAL_STATUSES =
            EnumSet.of(AdoptionStatus.TRIAL, AdoptionStatus.EXTENDS);

    @Override
    public Optional<AdoptionResponse> findAdoptById(Long adoptionId) {
        return adoptionRepository.findById(adoptionId)
                .map(adoptionDtoMapping::toResponse);
    }

    @Override
    public List<AdoptionResponse> findAllAdopt() {
        return adoptionRepository.findAll()
                .stream()
                .map(adoptionDtoMapping::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAdoptById(Long adoptionId) {
        if (!adoptionRepository.existsById(adoptionId)) {
            throw new AdoptionNotFoundException(adoptionId);
        }
        adoptionRepository.deleteById(adoptionId);
    }


    @Override
    @Transactional
    public AdoptionResponse createAdoption(CreateAdoptionRequest request) {
        if (request == null) {
            throw new AdoptionInvalidRequestException("Тело запроса пустое");
        }
        if (request.getAnimalId() == null || request.getUserId() == null) {
            throw new AdoptionInvalidRequestException("Обязательны animalId и userId");
        }

        Adoption adoption = adoptionDtoMapping.toAdoption(request);

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new AnimalNotFoundException(request.getAnimalId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AdoptionUserNotFoundException(request.getUserId()));

        if (animal.getStatus() != AnimalStatus.SEEKS_HOME) {
            throw new AnimalNotAvailableForAdoptionException(animal.getId(), animal.getStatus());
        }
        if (adoptionRepository.existsByAnimal_IdAndStatusIn(animal.getId(), ACTIVE_TRIAL_STATUSES)) {
            throw new AdoptionActiveTrialExistsException(animal.getId());
        }

        adoption.setAnimal(animal);
        adoption.setUser(user);

        Adoption savedAdoption = adoptionRepository.save(adoption);

        return adoptionDtoMapping.toResponse(savedAdoption);
    }

    @Override
    @Transactional
    public AdoptionResponse finishTrial(Long id) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new AdoptionNotFoundException(id));
        assertTrialActionAllowed(adoption, "завершение испытательного срока успехом");
        adoption.setStatus(AdoptionStatus.SUCCESS);
        return adoptionDtoMapping.toResponse(adoptionRepository.save(adoption));
    }

    @Override
    @Transactional
    public AdoptionResponse extendTrial(Long id, int daysToAdd) {
        if (daysToAdd <= 0) {
            throw new AdoptionTrialExtensionInvalidException(daysToAdd);
        }
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new AdoptionNotFoundException(id));
        assertTrialActionAllowed(adoption, "продление испытательного срока");
        if (adoption.getEndDate() == null) {
            throw new AdoptionEndDateMissingException(id);
        }
        adoption.setStatus(AdoptionStatus.EXTENDS);
        adoption.setEndDate(adoption.getEndDate().plusDays(daysToAdd));
        return adoptionDtoMapping.toResponse(adoptionRepository.save(adoption));
    }

    @Override
    @Transactional
    public AdoptionResponse failTrial(Long id) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new AdoptionNotFoundException(id));
        assertTrialActionAllowed(adoption, "завершение испытательного срока отказом");
        adoption.setStatus(AdoptionStatus.FAILED);
        return adoptionDtoMapping.toResponse(adoptionRepository.save(adoption));
    }

    private static void assertTrialActionAllowed(Adoption adoption, String action) {
        AdoptionStatus status = adoption.getStatus();
        if (status != AdoptionStatus.TRIAL && status != AdoptionStatus.EXTENDS) {
            throw new AdoptionInvalidStatusException(adoption.getId(), status, action);
        }
    }
}
