package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.dto.AnimalDto;
import com.example.nurseryAstana.model.enums.AnimalStatus;
import com.example.nurseryAstana.model.enums.Species;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест контроллера {@link AnimalController} c {@link TestRestTemplate}.
 * Особенность: используется {@link AnimalDto} без поля id,
 * поэтому тесты не проверяют идентификатор сущности и фокусируются
 * только на статус-кодах и доступных полях DTO.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void findAll_returnsOkAndBody() {
        ResponseEntity<AnimalDto[]> response =
                restTemplate.getForEntity(baseUrl() + "/animal", AnimalDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void createAnimal_andGetById() {
        AnimalDto dto = new AnimalDto();
        dto.setSpecies(Species.DOG);
        dto.setName("Barbos");
        dto.setAge(3);
        dto.setBreed("Ovcharka");
        dto.setDescription("Good boy");
        dto.setStatus(AnimalStatus.NOT_AVAILABLE);

        // POST /animal/create
        ResponseEntity<AnimalDto> createResponse =
                restTemplate.postForEntity(baseUrl() + "/animal/create", dto, AnimalDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        AnimalDto created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Barbos");
        assertThat(created.getSpecies()).isEqualTo(Species.DOG);
        // id не проверяется, т.к. в этих тестах AnimalDto без id
    }
}