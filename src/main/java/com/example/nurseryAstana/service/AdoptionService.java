package com.example.nurseryAstana.service;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AdoptionService {
    public Optional<AdoptionResponse> findAdoptById(Long adoptionid);
    public List<AdoptionResponse> findAllAdopt();
    public AdoptionResponse createAdoption(CreateAdoptionRequest adoptionDto);
    public void removeAdoptById(Long adoptionid);

    public Optional<AdoptionResponse> finishTrial(AdoptionResponse adoptionDto);
    public Optional<AdoptionResponse> extendTrial(AdoptionResponse adoptionDto);
    public Optional<AdoptionResponse> failTrial(AdoptionResponse adoptionDto);
}
