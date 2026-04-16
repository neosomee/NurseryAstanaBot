package com.example.nurseryAstana.service.impl;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.service.AdoptionService;

import java.util.Optional;

public class AdoptionServiceImpl implements AdoptionService {
    @Override
    public Optional<AdoptionResponse> getAdoptById(Long animalId) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionResponse> createAdoption(AdoptionResponse adoptionDto) {
        return Optional.empty();
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
