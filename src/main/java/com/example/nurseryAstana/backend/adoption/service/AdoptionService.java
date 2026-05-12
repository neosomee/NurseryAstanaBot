package com.example.nurseryAstana.backend.adoption.service;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AdoptionService {
    public Optional<AdoptionResponse> findAdoptById(Long adoptionid);
    public List<AdoptionResponse> findAllAdopt();
    public AdoptionResponse createAdoption(CreateAdoptionRequest adoptionDto);
    public void removeAdoptById(Long adoptionid);


    public Optional<AdoptionResponse> finishTrial(Long id);
    public Optional<AdoptionResponse> extendTrial(Long id, int daysToAdd);
    public Optional<AdoptionResponse> failTrial(Long id);
}
