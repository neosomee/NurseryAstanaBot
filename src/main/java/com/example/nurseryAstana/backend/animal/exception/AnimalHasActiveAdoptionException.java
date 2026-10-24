package com.example.nurseryAstana.backend.animal.exception;

public class AnimalHasActiveAdoptionException extends RuntimeException {

    public AnimalHasActiveAdoptionException(Long animalId) {
        super("У животного id=" + animalId + " есть активная заявка на усыновление (испытательный срок)");
    }
}
