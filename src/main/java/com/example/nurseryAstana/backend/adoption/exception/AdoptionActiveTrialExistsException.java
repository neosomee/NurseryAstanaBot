package com.example.nurseryAstana.backend.adoption.exception;

public class AdoptionActiveTrialExistsException extends RuntimeException {

    public AdoptionActiveTrialExistsException(Long animalId) {
        super("У животного id=" + animalId + " уже есть активный испытательный срок");
    }
}
