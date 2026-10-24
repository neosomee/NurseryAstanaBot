package com.example.nurseryAstana.backend.adoption.exception;

public class AdoptionNotFoundException extends RuntimeException {

    public AdoptionNotFoundException(Long id) {
        super("Заявка на усыновление не найдена: id=" + id);
    }
}
