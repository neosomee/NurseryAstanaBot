package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import com.example.nurseryAstana.service.impl.AdoptionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adoptions")
@AllArgsConstructor
public class AdoptionController {

    AdoptionServiceImpl adoptionService;

    @PostMapping
    public ResponseEntity<AdoptionResponse> createAdoption(@RequestBody CreateAdoptionRequest adoption) {
        return ResponseEntity.ok(adoptionService.createAdoption(adoption));
    }
}
