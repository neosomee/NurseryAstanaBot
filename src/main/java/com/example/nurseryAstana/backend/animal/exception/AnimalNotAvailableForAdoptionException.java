package com.example.nurseryAstana.backend.animal.exception;

import com.example.nurseryAstana.backend.animal.model.AnimalStatus;

public class AnimalNotAvailableForAdoptionException extends RuntimeException {

    public AnimalNotAvailableForAdoptionException(Long animalId, AnimalStatus status) {
        super("Животное id=" + animalId + " недоступно для усыновления при статусе " + status);
    }
}
