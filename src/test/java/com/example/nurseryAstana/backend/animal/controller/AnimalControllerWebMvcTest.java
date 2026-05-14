package com.example.nurseryAstana.backend.animal.controller;

import com.example.nurseryAstana.backend.animal.dto.AnimalResponse;
import com.example.nurseryAstana.backend.animal.dto.CreateAnimalRequest;
import com.example.nurseryAstana.backend.animal.model.AnimalStatus;
import com.example.nurseryAstana.backend.animal.model.Species;
import com.example.nurseryAstana.backend.animal.service.impl.AnimalServiceImple;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
class AnimalControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnimalServiceImple animalService;

    @Test
    void findAllReturnsAnimals() throws Exception {
        when(animalService.findAllAnimal()).thenReturn(List.of(animal(1L), animal(2L)));

        mockMvc.perform(get("/animal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].species").value("DOG"))
                .andExpect(jsonPath("$[0].name").value("Buddy"))
                .andExpect(jsonPath("$[0].age").value(3))
                .andExpect(jsonPath("$[0].breed").value("Mixed"))
                .andExpect(jsonPath("$[0].description").value("Friendly"))
                .andExpect(jsonPath("$[0].status").value("SEEKS_HOME"))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(animalService).findAllAnimal();
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void findByIdReturnsAnimalWhenPresent() throws Exception {
        when(animalService.findAnimalById(1L)).thenReturn(Optional.of(animal(1L)));

        mockMvc.perform(get("/animal/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy"));

        verify(animalService).findAnimalById(1L);
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void findByIdReturnsNotFoundWhenMissing() throws Exception {
        when(animalService.findAnimalById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/animal/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(animalService).findAnimalById(99L);
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void saveAnimalCreatesAnimal() throws Exception {
        when(animalService.createAnimal(any(CreateAnimalRequest.class))).thenReturn(animal(1L));

        mockMvc.perform(post("/animal/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAnimalRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.species").value("DOG"))
                .andExpect(jsonPath("$.status").value("SEEKS_HOME"));

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
    void updateAnimalUpdatesAnimal() throws Exception {
        when(animalService.updateAnimal(any(CreateAnimalRequest.class))).thenReturn(animal(1L));

        mockMvc.perform(put("/animal/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAnimalRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy"));

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
    void deleteAnimalRemovesByPathId() throws Exception {
        mockMvc.perform(delete("/animal/remove/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(animalService).removeAnimalById(1L);
        verifyNoMoreInteractions(animalService);
    }

    @Test
    void deleteAllAnimalRemovesAll() throws Exception {
        mockMvc.perform(delete("/animal/remove/all"))
                .andExpect(status().isNoContent());

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
