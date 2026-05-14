package com.example.nurseryAstana.backend.animal.controller;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.animal.model.Species;
import com.example.nurseryAstana.backend.controller.TestRestTemplateControllerTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AnimalControllerTestRestTemplateTest extends TestRestTemplateControllerTestSupport {

    @Test
    void findAllReturnsJsonAnimals() throws Exception {
        when(animalService.findAllAnimal()).thenReturn(List.of(animal(1L), animal(2L)));

        ResponseEntity<String> response = restTemplate.getForEntity("/animal", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body).hasSize(2);
        assertThat(body.get(0).get("id").asLong()).isEqualTo(1L);
        assertThat(body.get(0).get("species").asText()).isEqualTo("DOG");
        assertThat(body.get(0).get("name").asText()).isEqualTo("Buddy");
        assertThat(body.get(0).get("age").asInt()).isEqualTo(3);
        assertThat(body.get(0).get("breed").asText()).isEqualTo("Mixed");
        assertThat(body.get(0).get("description").asText()).isEqualTo("Friendly");
        assertThat(body.get(0).get("status").asText()).isEqualTo("SEEKS_HOME");
        assertThat(body.get(1).get("id").asLong()).isEqualTo(2L);

        verify(animalService).findAllAnimal();
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void findByIdReturnsJsonAnimal() throws Exception {
        when(animalService.findAnimalById(1L)).thenReturn(Optional.of(animal(1L)));

        ResponseEntity<String> response = restTemplate.getForEntity("/animal/{id}", String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("name").asText()).isEqualTo("Buddy");

        verify(animalService).findAnimalById(1L);
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() {
        when(animalService.findAnimalById(99L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.getForEntity("/animal/{id}", String.class, 99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(animalService).findAnimalById(99L);
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void saveAnimalAcceptsJsonAndReturnsJsonAnimal() throws Exception {
        when(animalService.createAnimal(any(CreateAnimalRequest.class))).thenReturn(animal(1L));

        ResponseEntity<String> response = restTemplate.postForEntity("/animal/create", createAnimalRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("species").asText()).isEqualTo("DOG");
        assertThat(body.get("status").asText()).isEqualTo("SEEKS_HOME");

        verify(animalService).createAnimal(argThat(request ->
                request.getSpecies() == Species.DOG
                        && "Buddy".equals(request.getName())
                        && request.getAge().equals(3)
                        && "Mixed".equals(request.getBreed())
                        && "Friendly".equals(request.getDescription())
                        && request.getStatus() == AnimalStatus.SEEKS_HOME));
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void updateAnimalAcceptsJsonAndReturnsJsonAnimal() throws Exception {
        when(animalService.updateAnimal(any(CreateAnimalRequest.class))).thenReturn(animal(1L));

        ResponseEntity<String> response = restTemplate.exchange(
                "/animal/update",
                HttpMethod.PUT,
                new HttpEntity<>(createAnimalRequest()),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body.get("id").asLong()).isEqualTo(1L);
        assertThat(body.get("name").asText()).isEqualTo("Buddy");

        verify(animalService).updateAnimal(argThat(request ->
                request.getSpecies() == Species.DOG
                        && "Buddy".equals(request.getName())
                        && request.getAge().equals(3)
                        && "Mixed".equals(request.getBreed())
                        && "Friendly".equals(request.getDescription())
                        && request.getStatus() == AnimalStatus.SEEKS_HOME));
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void deleteAnimalReturnsNoContent() {
        ResponseEntity<Void> response = restTemplate.exchange("/animal/remove/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(animalService).removeAnimalById(1L);
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void deleteAllAnimalReturnsNoContent() {
        ResponseEntity<Void> response = restTemplate.exchange("/animal/remove/all", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(animalService).removeAllAnimals();
        verifyNoMoreInteractions(animalService);
    }

    private static AnimalResponse animal(Long id) {
        AnimalResponse response = new AnimalResponse();
        response.setId(id);
        response.setSpecies(Species.DOG);
        response.setName("Buddy");
        response.setAge(3);
        response.setBreed("Mixed");
        response.setDescription("Friendly");
        response.setStatus(AnimalStatus.SEEKS_HOME);
        return response;
    }

    private static CreateAnimalRequest createAnimalRequest() {
        CreateAnimalRequest request = new CreateAnimalRequest();
        request.setSpecies(Species.DOG);
        request.setName("Buddy");
        request.setAge(3);
        request.setBreed("Mixed");
        request.setDescription("Friendly");
        request.setStatus(AnimalStatus.SEEKS_HOME);
        return request;
    }
}
