package com.example.nurseryAstana.model.enums;

public enum AnimalStatus {
    SEEKS_HOME("Ищет дом"),
    IN_FOSTER("На передержке"),
    QUARANTINE("На карантине"),
    RESERVED("Забронирован"),
    ADOPTED("Уехал в семью"),
    NOT_AVAILABLE("Не ищет дом");

    AnimalStatus(String s) {
    }
}
