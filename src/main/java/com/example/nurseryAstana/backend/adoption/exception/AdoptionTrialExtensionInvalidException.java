package com.example.nurseryAstana.backend.adoption.exception;

public class AdoptionTrialExtensionInvalidException extends RuntimeException {

    public AdoptionTrialExtensionInvalidException(int daysToAdd) {
        super("Некорректное продление испытательного срока: daysToAdd=" + daysToAdd);
    }
}
