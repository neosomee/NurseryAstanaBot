package com.example.nurseryAstana.service.impl;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import com.example.nurseryAstana.dto.mapper.AdoptionMapping;
import com.example.nurseryAstana.model.Adoption;
import com.example.nurseryAstana.model.Animal;
import com.example.nurseryAstana.model.User;
import com.example.nurseryAstana.repository.AdoptionRepository;
import com.example.nurseryAstana.repository.AnimalRepository;
import com.example.nurseryAstana.repository.UserRepository;
import com.example.nurseryAstana.service.AdoptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    private final AdoptionMapping adoptionMapping;

    @Override
    public Optional<AdoptionResponse> getAdoptById(Long adoptionId) {
        return adoptionRepository.findById(adoptionId)
                .map(adoptionMapping::toResponse);
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
    public Optional<AdoptionResponse> finishTrial(AdoptionResponse adoptionDto) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionResponse> extendTrial(AdoptionResponse adoptionDto) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionResponse> failTrial(AdoptionResponse adoptionDto) {
        return Optional.empty();
    }
}
