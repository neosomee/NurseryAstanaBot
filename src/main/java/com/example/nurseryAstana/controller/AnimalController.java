package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.model.Animal;
import com.example.nurseryAstana.service.AnimalService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animal")
@AllArgsConstructor
public class AnimalController {

    AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<Animal>> findAll() {
        List<Animal> animals =animalService.findAllAnimal();
        return ResponseEntity.ok(animals);
    }

    @GetMapping("{id}")
    public ResponseEntity<Animal> findById(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.findAnimalById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Animal> saveAnimal(@RequestBody Animal animal) {
        return ResponseEntity.ok(animalService.createAnimal(animal));
    }

    @PutMapping("/update")
    public ResponseEntity<Animal> updateAnimal(@RequestBody Animal animal) {
        return ResponseEntity.ok(animalService.updateAnimal(animal));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteAnimal(@RequestBody Long id) {
        animalService.removeAnimalById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAnimal() {
        animalService.removeAllAnimals();
        return ResponseEntity.noContent().build();
    }


}
