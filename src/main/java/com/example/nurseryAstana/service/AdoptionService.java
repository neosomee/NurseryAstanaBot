package com.example.nurseryAstana.service;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AdoptionService {
    public Optional<AdoptionResponse> getAdoptById(Long adoptionid);
    public AdoptionResponse createAdoption(CreateAdoptionRequest adoptionDto);

    public Optional<AdoptionResponse> finishTrial(AdoptionResponse adoptionDto);
    public Optional<AdoptionResponse> extendTrial(AdoptionResponse adoptionDto);
    public Optional<AdoptionResponse> failTrial(AdoptionResponse adoptionDto);
}
