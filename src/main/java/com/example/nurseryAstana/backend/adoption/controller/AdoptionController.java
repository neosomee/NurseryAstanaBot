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
    public ResponseEntity<AdoptionResponse> getAllAdoption(@PathVariable Long id){
        Optional<AdoptionResponse> adoptionResponse = adoptionService.findAdoptById(id);
        return adoptionResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
        Optional<AdoptionResponse> adoptionResponse = adoptionService.finishTrial(id);
        return adoptionResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/trialdays/extend/{id}")
    public ResponseEntity<AdoptionResponse> trialdaysExtend(@PathVariable Long id, @RequestBody int extendId) {
        Optional<AdoptionResponse> adoptionResponse = adoptionService.extendTrial(id, extendId);
        return adoptionResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/trialdays/fail/{id}")
    public ResponseEntity<AdoptionResponse> trialdaysFail(@PathVariable Long id) {
        Optional<AdoptionResponse> adoptionResponse = adoptionService.failTrial(id);
        return adoptionResponse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
