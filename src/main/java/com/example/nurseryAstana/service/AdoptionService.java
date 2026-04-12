package com.example.nurseryAstana.service;

import com.example.nurseryAstana.dto.AdoptionDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AdoptionService {
    public Optional<AdoptionDto> getAdoptById(Long animalId);
    public Optional<AdoptionDto> createAdoption(AdoptionDto adoptionDto);

    public Optional<AdoptionDto> finishTrial(AdoptionDto adoptionDto);
    public Optional<AdoptionDto> extendTrial(AdoptionDto adoptionDto);
    public Optional<AdoptionDto> failTrial(AdoptionDto adoptionDto);
}
