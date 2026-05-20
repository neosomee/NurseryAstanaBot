package com.example.nurseryAstana.backend.adoption.controller;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.controller.TestRestTemplateControllerTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AdoptionControllerTestRestTemplateTest extends TestRestTemplateControllerTestSupport {

    @Test
    void findAllAdoptReturnsJsonAdoptions() throws Exception {
        when(adoptionService.findAllAdopt()).thenReturn(List.of(adoption(1L, AdoptionStatus.TRIAL), adoption(2L, AdoptionStatus.SUCCESS)));

        ResponseEntity<String> response = restTemplate.getForEntity("/adoptions", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body).hasSize(2);
        assertThat(body.get(0).get("id").asLong()).isEqualTo(1L);
        assertThat(body.get(0).get("animalId").asLong()).isEqualTo(10L);
        assertThat(body.get(0).get("userId").asLong()).isEqualTo(20L);
        assertThat(body.get(0).get("startDate").asText()).startsWith("2026-05-01T10:00");
        assertThat(body.get(0).get("endDate").asText()).startsWith("2026-05-15T10:00");
        assertThat(body.get(0).get("status").asText()).isEqualTo("TRIAL");
        assertThat(body.get(1).get("status").asText()).isEqualTo("SUCCESS");

        verify(adoptionService).findAllAdopt();
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void getAllAdoptionReturnsJsonAdoption() throws Exception {
        when(adoptionService.findAdoptById(1L)).thenReturn(Optional.of(adoption(1L, AdoptionStatus.TRIAL)));

        ResponseEntity<String> response = restTemplate.getForEntity("/adoptions/{id}", String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("status").asText()).isEqualTo("TRIAL");

        verify(adoptionService).findAdoptById(1L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void getAllAdoptionReturnsNotFoundWhenMissing() {
        when(adoptionService.findAdoptById(99L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.getForEntity("/adoptions/{id}", String.class, 99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(adoptionService).findAdoptById(99L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void createAdoptionAcceptsJsonAndReturnsJsonAdoption() throws Exception {
        when(adoptionService.createAdoption(any(CreateAdoptionRequest.class))).thenReturn(adoption(1L, AdoptionStatus.TRIAL));

        ResponseEntity<String> response = restTemplate.postForEntity("/adoptions/create", createAdoptionRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("animalId").asLong()).isEqualTo(10L);
        assertThat(body.get("userId").asLong()).isEqualTo(20L);

        verify(adoptionService).createAdoption(argThat(request ->
                request.getAnimalId().equals(10L) && request.getUserId().equals(20L)));
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void removeAdoptionReturnsNoContent() {
        ResponseEntity<Void> response = restTemplate.exchange("/adoptions/remove/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(adoptionService).removeAdoptById(1L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void trialdaysSuccessReturnsJsonAdoption() throws Exception {
        when(adoptionService.finishTrial(1L)).thenReturn(adoption(1L, AdoptionStatus.SUCCESS));

        ResponseEntity<String> response = restTemplate.exchange("/adoptions/trialdays/success/{id}", HttpMethod.PUT, HttpEntity.EMPTY, String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        assertThat(objectMapper.readTree(response.getBody()).get("status").asText()).isEqualTo("SUCCESS");
        verify(adoptionService).finishTrial(1L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void trialdaysExtendAcceptsJsonNumberAndReturnsJsonAdoption() throws Exception {
        when(adoptionService.extendTrial(1L, 5)).thenReturn(adoption(1L, AdoptionStatus.EXTENDS));

        ResponseEntity<String> response = restTemplate.exchange(
                "/adoptions/trialdays/extend/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(5),
                String.class,
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        assertThat(objectMapper.readTree(response.getBody()).get("status").asText()).isEqualTo("EXTENDS");
        verify(adoptionService).extendTrial(1L, 5);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void trialdaysFailReturnsJsonAdoption() throws Exception {
        when(adoptionService.failTrial(1L)).thenReturn(adoption(1L, AdoptionStatus.FAILED));

        ResponseEntity<String> response = restTemplate.exchange("/adoptions/trialdays/fail/{id}", HttpMethod.PUT, HttpEntity.EMPTY, String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        assertThat(objectMapper.readTree(response.getBody()).get("status").asText()).isEqualTo("FAILED");
        verify(adoptionService).failTrial(1L);
        verifyNoMoreInteractions(adoptionService);
    }

    private static AdoptionResponse adoption(Long id, AdoptionStatus status) {
        AdoptionResponse response = new AdoptionResponse();
        response.setId(id);
        response.setAnimalId(10L);
        response.setUserId(20L);
        response.setStartDate(LocalDateTime.of(2026, 5, 1, 10, 0));
        response.setEndDate(LocalDateTime.of(2026, 5, 15, 10, 0));
        response.setStatus(status);
        return response;
    }

    private static CreateAdoptionRequest createAdoptionRequest() {
        CreateAdoptionRequest request = new CreateAdoptionRequest();
        request.setAnimalId(10L);
        request.setUserId(20L);
        return request;
    }
}
