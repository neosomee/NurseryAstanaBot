package com.example.nurseryAstana.service.impl;

import com.example.nurseryAstana.dto.AdoptionDto;
import com.example.nurseryAstana.service.AdoptionService;

import java.util.Optional;

public class AdoptionServiceImpl implements AdoptionService {
    @Override
    public Optional<AdoptionDto> getAdoptById(Long animalId) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionDto> createAdoption(AdoptionDto adoptionDto) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionDto> finishTrial(AdoptionDto adoptionDto) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionDto> extendTrial(AdoptionDto adoptionDto) {
        return Optional.empty();
    }

    @Override
    public Optional<AdoptionDto> failTrial(AdoptionDto adoptionDto) {
        return Optional.empty();
    }
}
