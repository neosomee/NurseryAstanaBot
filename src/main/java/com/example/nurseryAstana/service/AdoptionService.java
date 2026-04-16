package com.example.nurseryAstana.service;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AdoptionService {
    public Optional<AdoptionResponse> getAdoptById(Long animalId);
    public Optional<AdoptionResponse> createAdoption(AdoptionResponse adoptionDto);

    public Optional<AdoptionResponse> finishTrial(AdoptionResponse adoptionDto);
    public Optional<AdoptionResponse> extendTrial(AdoptionResponse adoptionDto);
    public Optional<AdoptionResponse> failTrial(AdoptionResponse adoptionDto);
}
