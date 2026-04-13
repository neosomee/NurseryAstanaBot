package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.dto.AnimalDto;
import com.example.nurseryAstana.model.enums.AnimalStatus;
import com.example.nurseryAstana.model.enums.Species;
import com.example.nurseryAstana.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvc-тест для {@link AnimalController}.
 * Особенность: используется {@link AnimalDto} без поля id,
 * поэтому в проверках ассерты делаются только по доступным полям
 * (species, name, age, breed, description, status), а не по идентификатору.
 */
@WebMvcTest(AnimalController.class)
class AnimalControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnimalService animalService;

    @Test
    void findAll_returnsList() throws Exception {
        AnimalDto dto = new AnimalDto();
        dto.setSpecies(Species.DOG);
        dto.setName("Barbos");
        dto.setAge(3);
        dto.setBreed("Ovcharka");
        dto.setDescription("Good boy");
        dto.setStatus(AnimalStatus.NOT_AVAILABLE);

        when(animalService.findAllAnimal()).thenReturn(List.of(dto));

        mockMvc.perform(get("/animal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].species").value("DOG"))
                .andExpect(jsonPath("$[0].name").value("Barbos"))
                .andExpect(jsonPath("$[0].age").value(3))
                .andExpect(jsonPath("$[0].breed").value("Ovcharka"))
                .andExpect(jsonPath("$[0].description").value("Good boy"))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void findById_returnsDto() throws Exception {
        Long id = 1L;

        AnimalDto dto = new AnimalDto();
        dto.setSpecies(Species.CAT);
        dto.setName("Murka");
        dto.setAge(2);
        dto.setBreed("Siam");
        dto.setDescription("Lazy cat");
        dto.setStatus(AnimalStatus.ADOPTED);

        when(animalService.findAnimalById(id)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/animal/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.species").value("CAT"))
                .andExpect(jsonPath("$.name").value("Murka"))
                .andExpect(jsonPath("$.age").value(2))
                .andExpect(jsonPath("$.breed").value("Siam"))
                .andExpect(jsonPath("$.description").value("Lazy cat"))
                .andExpect(jsonPath("$.status").value("ADOPTED"));
    }
}