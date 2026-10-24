package com.example.nurseryAstana.backend.report.controller;

import com.example.nurseryAstana.backend.controller.TestRestTemplateControllerTestSupport;
import com.example.nurseryAstana.backend.report.model.Report;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ReportPreviewControllerTestRestTemplateTest extends TestRestTemplateControllerTestSupport {

    private static final Path UPLOAD_DIR = createUploadDir();

    @DynamicPropertySource
    static void registerPreviewProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload.reports-dir", () -> UPLOAD_DIR.toString());
        registry.add("app.upload-dir", () -> UPLOAD_DIR.toString());
    }

    @BeforeEach
    void setUpFiles() throws IOException {
        cleanUploadDir();
        Files.createDirectories(UPLOAD_DIR);
    }

    @AfterAll
    static void tearDownFiles() throws IOException {
        cleanUploadDir();
        Files.deleteIfExists(UPLOAD_DIR);
    }

    @Test
    void getReportPreviewReturnsJpegBytesWhenFileExists() throws IOException {
        byte[] bytes = new byte[]{1, 2, 3, 4};
        Files.write(UPLOAD_DIR.resolve("report.jpg"), bytes);
        when(reportService.findById(1L)).thenReturn(Optional.of(report("/files/report.jpg")));

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "/api/reports/{id}/preview",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
        assertThat(response.getHeaders().getContentLength()).isEqualTo(bytes.length);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo("inline; filename=\"report.jpg\"");
        assertThat(response.getBody()).isEqualTo(bytes);

        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsNotFoundWhenReportMissing() {
        when(reportService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = restTemplate.getForEntity("/api/reports/{id}/preview", String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsNotFoundWhenPhotoUrlIsNull() {
        when(reportService.findById(1L)).thenReturn(Optional.of(report(null)));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/reports/{id}/preview", String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsNotFoundWhenFileDoesNotExist() {
        when(reportService.findById(1L)).thenReturn(Optional.of(report("/files/missing.jpg")));

        ResponseEntity<String> response = restTemplate.getForEntity("/api/reports/{id}/preview", String.class, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    private static Report report(String photoUrl) {
        Report report = new Report();
        report.setId(1L);
        report.setPhotoUrl(photoUrl);
        return report;
    }

    private static Path createUploadDir() {
        try {
            return Files.createTempDirectory("nursery-astana-preview-resttemplate-");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create preview test directory", e);
        }
    }

    private static void cleanUploadDir() throws IOException {
        if (!Files.exists(UPLOAD_DIR)) {
            return;
        }
        try (var paths = Files.walk(UPLOAD_DIR)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                if (!path.equals(UPLOAD_DIR)) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }
}
