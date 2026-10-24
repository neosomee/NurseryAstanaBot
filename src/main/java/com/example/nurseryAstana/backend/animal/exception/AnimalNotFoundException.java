package com.example.nurseryAstana.backend.animal.exception;

public class AnimalNotFoundException extends RuntimeException {

    public AnimalNotFoundException(Long id) {
        super("Животное не найдено: id=" + id);
    }
}
