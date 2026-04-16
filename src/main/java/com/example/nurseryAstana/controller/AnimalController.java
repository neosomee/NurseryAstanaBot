package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.dto.animal.AnimalDto;
import com.example.nurseryAstana.service.impl.AnimalService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animal")
@AllArgsConstructor
public class AnimalController {

    AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<AnimalDto>> findAll() {
        List<AnimalDto> animals =animalService.findAllAnimal();
        return ResponseEntity.ok(animals);
    }

    @GetMapping("{id}")
    public ResponseEntity<Optional<AnimalDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.findAnimalById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<AnimalDto> saveAnimal(@RequestBody AnimalDto animal) {
        return ResponseEntity.ok(animalService.createAnimal(animal));
    }

    @PutMapping("/update")
    public ResponseEntity<AnimalDto> updateAnimal(@RequestBody AnimalDto animal) {
        return ResponseEntity.ok(animalService.updateAnimal(animal));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteAnimal(@RequestBody Long id) {
        animalService.removeAnimalById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/all")
    public ResponseEntity<Void> deleteAllAnimal() {
        animalService.removeAllAnimals();
        return ResponseEntity.noContent().build();
    }


}
