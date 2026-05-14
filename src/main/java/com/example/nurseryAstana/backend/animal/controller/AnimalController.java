package com.example.nurseryAstana.backend.animal.controller;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.service.impl.AnimalServiceImple;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animal")
@AllArgsConstructor
public class AnimalController {

    AnimalServiceImple animalService;

    @GetMapping
    public ResponseEntity<List<AnimalResponse>> findAll() {
        List<AnimalResponse> animals =animalService.findAllAnimal();
        return ResponseEntity.ok(animals);
    }

    @GetMapping("{id}")
    public ResponseEntity<AnimalResponse> findById(@PathVariable Long id) {
        return animalService.findAnimalById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<AnimalResponse> saveAnimal(@RequestBody CreateAnimalRequest animal) {
        return ResponseEntity.ok(animalService.createAnimal(animal));
    }

    @PutMapping("/update")
    public ResponseEntity<AnimalResponse> updateAnimal(@RequestBody CreateAnimalRequest animal) {
        return ResponseEntity.ok(animalService.updateAnimal(animal));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalService.removeAnimalById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/all")
    public ResponseEntity<Void> deleteAllAnimal() {
        animalService.removeAllAnimals();
        return ResponseEntity.noContent().build();
    }


}
