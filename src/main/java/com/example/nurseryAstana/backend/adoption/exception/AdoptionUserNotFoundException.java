package com.example.nurseryAstana.backend.adoption.exception;

public class AdoptionUserNotFoundException extends RuntimeException {

    public AdoptionUserNotFoundException(Long userId) {
        super("Пользователь для заявки не найден: userId=" + userId);
    }
}
