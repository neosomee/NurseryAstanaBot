package com.example.nurseryAstana.backend.adoption.exception;

public class AdoptionEndDateMissingException extends RuntimeException {

    public AdoptionEndDateMissingException(Long adoptionId) {
        super("У заявки id=" + adoptionId + " не задана дата окончания испытательного срока");
    }
}
