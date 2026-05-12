package com.example.nurseryAstana.service.impl;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import com.example.nurseryAstana.dto.mapper.AdoptionMapping;
import com.example.nurseryAstana.model.Adoption;
import com.example.nurseryAstana.backend.animal.model.Animal;
import com.example.nurseryAstana.model.User;
import com.example.nurseryAstana.model.enums.AdoptionStatus;
import com.example.nurseryAstana.repository.AdoptionRepository;
import com.example.nurseryAstana.repository.AnimalRepository;
import com.example.nurseryAstana.repository.UserRepository;
import com.example.nurseryAstana.service.AdoptionService;
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

    private final AdoptionMapping adoptionMapping;

    @Override
    public Optional<AdoptionResponse> findAdoptById(Long adoptionId) {
        return adoptionRepository.findById(adoptionId)
                .map(adoptionMapping::toResponse);
    }

    @Override
    public List<AdoptionResponse> findAllAdopt() {
        return adoptionRepository.findAll()
                .stream()
                .map(adoptionMapping::toResponse)
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
        Adoption adoption = adoptionMapping.toAdoption(request);

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
        return adoptionMapping.toResponse(savedAdoption);
    }

    @Override
    public Optional<AdoptionResponse> finishTrial(Long id) {

        return adoptionRepository.findById(id).map(adoption -> {
            adoption.setStatus(AdoptionStatus.SUCCESS);
            return adoptionRepository.save(adoption);
        })
                .map(adoptionMapping::toResponse);
    }

    @Override
    public Optional<AdoptionResponse> extendTrial(Long id, int daysToAdd) {
        return adoptionRepository.findById(id).map(
                adoption -> {
                    adoption.setStatus(AdoptionStatus.EXTENDS);
                    adoption.setEndDate(adoption.getEndDate().plusDays(daysToAdd));
                    return adoptionRepository.save(adoption);
                }
        ).map(adoptionMapping::toResponse);
    }

    @Override
    public Optional<AdoptionResponse> failTrial(Long id) {
        return adoptionRepository.findById(id).map(adoption -> {
            adoption.setStatus(AdoptionStatus.FAILED);
            return adoptionRepository.save(adoption);
        }).map(adoptionMapping::toResponse);
    }
}
