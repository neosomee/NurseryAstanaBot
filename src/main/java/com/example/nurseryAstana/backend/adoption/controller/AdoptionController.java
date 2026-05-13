package com.example.nurseryAstana.backend.adoption.controller;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.service.imple.AdoptionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/adoptions")
@AllArgsConstructor
public class AdoptionController {

    AdoptionServiceImpl adoptionService;

    @GetMapping
    public ResponseEntity<List<AdoptionResponse>> findAllAdopt() {
        List<AdoptionResponse> adoptionResponseList = adoptionService.findAllAdopt();
        return ResponseEntity.ok(adoptionResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<AdoptionResponse>> getAllAdoption(@PathVariable Long id){
        Optional<AdoptionResponse> adoptionResponse = adoptionService.findAdoptById(id);
        return ResponseEntity.ok(adoptionResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<AdoptionResponse> createAdoption(@RequestBody CreateAdoptionRequest adoption) {
        return ResponseEntity.ok(adoptionService.createAdoption(adoption));
    }
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<AdoptionResponse> removeAdoption(@PathVariable Long id){
        adoptionService.removeAdoptById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/trialdays/success/{id}")
    public ResponseEntity<AdoptionResponse> trialdaysSuccess(@PathVariable Long id) {
        return ResponseEntity.ok(adoptionService.finishTrial(id));
    }

    @PutMapping("/trialdays/extend/{id}")
    public ResponseEntity<AdoptionResponse> trialdaysExtend(@PathVariable Long id, @RequestBody int extendId) {
        return ResponseEntity.ok(adoptionService.extendTrial(id, extendId));
    }

    @PutMapping("/trialdays/fail/{id}")
    public ResponseEntity<AdoptionResponse> trialdaysFail(@PathVariable Long id) {
        return ResponseEntity.ok(adoptionService.failTrial(id));
    }
}
