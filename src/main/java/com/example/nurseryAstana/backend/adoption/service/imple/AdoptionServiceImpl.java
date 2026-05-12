package com.example.nurseryAstana.backend.adoption.service.imple;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.mapper.AdoptionDtoMapping;
import com.example.nurseryAstana.backend.adoption.model.Adoption;
import com.example.nurseryAstana.backend.adoption.service.AdoptionService;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.backend.user.model.User;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.repository.AdoptionRepository;
import com.example.nurseryAstana.backend.repository.AnimalRepository;
import com.example.nurseryAstana.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        adoptionRepository.deleteById(adoptionId);
    }


    @Override
    @Transactional
    public AdoptionResponse createAdoption(CreateAdoptionRequest request) {
        // Маппинг
        Adoption adoption = adoptionDtoMapping.toAdoption(request);

        // Установка связей
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new EntityNotFoundException("Animal not found with id: " + request.getAnimalId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        adoption.setAnimal(animal);
        adoption.setUser(user);

        // Сохранение
        Adoption savedAdoption = adoptionRepository.save(adoption);

        // Возврат Response
        return adoptionDtoMapping.toResponse(savedAdoption);
    }

    @Override
    public Optional<AdoptionResponse> finishTrial(Long id) {

        return adoptionRepository.findById(id).map(adoption -> {
            adoption.setStatus(AdoptionStatus.SUCCESS);
            return adoptionRepository.save(adoption);
        })
                .map(adoptionDtoMapping::toResponse);
    }

    @Override
    public Optional<AdoptionResponse> extendTrial(Long id, int daysToAdd) {
        return adoptionRepository.findById(id).map(
                adoption -> {
                    adoption.setStatus(AdoptionStatus.EXTENDS);
                    adoption.setEndDate(adoption.getEndDate().plusDays(daysToAdd));
                    return adoptionRepository.save(adoption);
                }
        ).map(adoptionDtoMapping::toResponse);
    }

    @Override
    public Optional<AdoptionResponse> failTrial(Long id) {
        return adoptionRepository.findById(id).map(adoption -> {
            adoption.setStatus(AdoptionStatus.FAILED);
            return adoptionRepository.save(adoption);
        }).map(adoptionDtoMapping::toResponse);
    }
}
