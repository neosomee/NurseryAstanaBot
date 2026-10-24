package com.example.nurseryAstana.backend.common.exception;

import com.example.nurseryAstana.backend.adoption.exception.AdoptionActiveTrialExistsException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionEndDateMissingException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionInvalidRequestException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionInvalidStatusException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionNotFoundException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionTrialExtensionInvalidException;
import com.example.nurseryAstana.backend.adoption.exception.AdoptionUserNotFoundException;
import com.example.nurseryAstana.backend.animal.exception.AnimalHasActiveAdoptionException;
import com.example.nurseryAstana.backend.animal.exception.AnimalInvalidDataException;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotAvailableForAdoptionException;
import com.example.nurseryAstana.backend.animal.exception.AnimalNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AnimalNotFoundException.class)
    public ResponseEntity<ErrorResponse> animalNotFound(AnimalNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(AnimalInvalidDataException.class)
    public ResponseEntity<ErrorResponse> animalInvalidData(AnimalInvalidDataException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(AnimalNotAvailableForAdoptionException.class)
    public ResponseEntity<ErrorResponse> animalNotAvailable(AnimalNotAvailableForAdoptionException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(AnimalHasActiveAdoptionException.class)
    public ResponseEntity<ErrorResponse> animalHasAdoption(AnimalHasActiveAdoptionException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> adoptionNotFound(AdoptionNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> adoptionUserNotFound(AdoptionUserNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionInvalidRequestException.class)
    public ResponseEntity<ErrorResponse> adoptionInvalidRequest(AdoptionInvalidRequestException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> adoptionInvalidStatus(AdoptionInvalidStatusException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionActiveTrialExistsException.class)
    public ResponseEntity<ErrorResponse> adoptionActiveTrial(AdoptionActiveTrialExistsException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionTrialExtensionInvalidException.class)
    public ResponseEntity<ErrorResponse> adoptionTrialExtend(AdoptionTrialExtensionInvalidException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(AdoptionEndDateMissingException.class)
    public ResponseEntity<ErrorResponse> adoptionEndDate(AdoptionEndDateMissingException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    private static ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
