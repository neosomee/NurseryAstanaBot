package com.example.nurseryAstana.backend.adoption.exception;

import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;

public class AdoptionInvalidStatusException extends RuntimeException {

    public AdoptionInvalidStatusException(Long adoptionId, AdoptionStatus current, String action) {
        super("Для заявки id=" + adoptionId + " действие «" + action + "» недопустимо при статусе " + current);
    }
}
