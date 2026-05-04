package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.dto.adoption.AdoptionResponse;
import com.example.nurseryAstana.dto.adoption.CreateAdoptionRequest;
import com.example.nurseryAstana.service.impl.AdoptionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    public ResponseEntity<Optional<AdoptionResponse>> getAllAdoption(@RequestBody Long id){
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
}
