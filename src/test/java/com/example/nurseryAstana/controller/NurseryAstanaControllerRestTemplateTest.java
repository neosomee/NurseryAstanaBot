package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NurseryAstanaControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void getAllUsers() {
        String baseUrl = "http://localhost:" + port;
        ResponseEntity<User[]> response =
                restTemplate.getForEntity(baseUrl + "/api/users", User[].class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }
}