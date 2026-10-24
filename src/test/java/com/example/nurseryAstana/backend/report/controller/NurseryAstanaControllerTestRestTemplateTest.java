package com.example.nurseryAstana.backend.report.controller;

import com.example.nurseryAstana.backend.controller.TestRestTemplateControllerTestSupport;
import com.example.nurseryAstana.backend.report.model.Report;
import com.example.nurseryAstana.backend.user.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class NurseryAstanaControllerTestRestTemplateTest extends TestRestTemplateControllerTestSupport {

    @Test
    void getAllUsersReturnsJsonUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setTelegramId(12345L);
        user.setUsername("alice");

        when(userService.findAllUsers()).thenReturn(List.of(user));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/users", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body).hasSize(1);
        assertThat(body.get(0).get("id").asLong()).isEqualTo(1L);
        assertThat(body.get(0).get("telegramId").asLong()).isEqualTo(12345L);
        assertThat(body.get(0).get("username").asText()).isEqualTo("alice");

        verify(userService).findAllUsers();
        verifyNoMoreInteractions(userService, reportService);
    }

    @Test
    void getAllUsersReturnsEmptyJsonArray() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/users", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        assertThat(objectMapper.readTree(response.getBody())).isEmpty();
        verify(userService).findAllUsers();
        verifyNoMoreInteractions(userService, reportService);
    }

    @Test
    void getAllReportsReturnsJsonReports() throws Exception {
        Report report = new Report();
        report.setId(10L);
        report.setText("Need help");
        report.setPhotoUrl("/files/report.jpg");
        report.setCreatedAt(LocalDateTime.of(2026, 5, 14, 12, 0));

        when(reportService.findAllReports()).thenReturn(List.of(report));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/reports", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(body).hasSize(1);
        assertThat(body.get(0).get("id").asLong()).isEqualTo(10L);
        assertThat(body.get(0).get("text").asText()).isEqualTo("Need help");
        assertThat(body.get(0).get("photoUrl").asText()).isEqualTo("/files/report.jpg");
        assertThat(body.get(0).get("createdAt").asText()).startsWith("2026-05-14T12:00");
        assertThat(body.get(0).has("user")).isFalse();

        verify(reportService).findAllReports();
        verifyNoMoreInteractions(userService, reportService);
    }

    @Test
    void getAllReportsReturnsEmptyJsonArray() throws Exception {
        when(reportService.findAllReports()).thenReturn(List.of());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/reports", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertJsonContentType(response.getHeaders().getContentType());
        assertThat(objectMapper.readTree(response.getBody())).isEmpty();
        verify(reportService).findAllReports();
        verifyNoMoreInteractions(userService, reportService);
    }
}
