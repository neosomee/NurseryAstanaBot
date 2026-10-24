package com.example.nurseryAstana.backend.report.controller;

import com.example.nurseryAstana.backend.report.model.Report;
import com.example.nurseryAstana.backend.report.service.ReportService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportPreviewController.class)
class ReportPreviewControllerWebMvcTest {

    private static final Path UPLOAD_DIR = createUploadDir();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload.reports-dir", () -> UPLOAD_DIR.toString());
    }

    @BeforeEach
    void setUp() throws IOException {
        cleanUploadDir();
        Files.createDirectories(UPLOAD_DIR);
    }

    @AfterAll
    static void tearDown() throws IOException {
        cleanUploadDir();
        Files.deleteIfExists(UPLOAD_DIR);
    }

    @Test
    void getReportPreviewReturnsNotFoundWhenReportMissing() throws Exception {
        when(reportService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reports/{id}/preview", 1L))
                .andExpect(status().isNotFound());

        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsNotFoundWhenPhotoUrlIsNull() throws Exception {
        when(reportService.findById(1L)).thenReturn(Optional.of(report(null)));

        mockMvc.perform(get("/api/reports/{id}/preview", 1L))
                .andExpect(status().isNotFound());

        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsNotFoundWhenFileDoesNotExist() throws Exception {
        when(reportService.findById(1L)).thenReturn(Optional.of(report("/files/missing.jpg")));

        mockMvc.perform(get("/api/reports/{id}/preview", 1L))
                .andExpect(status().isNotFound());

        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsInternalServerErrorWhenFileCannotBeRead() throws Exception {
        Files.createDirectory(UPLOAD_DIR.resolve("directory.jpg"));
        when(reportService.findById(1L)).thenReturn(Optional.of(report("/files/directory.jpg")));

        mockMvc.perform(get("/api/reports/{id}/preview", 1L))
                .andExpect(status().isInternalServerError());

        verify(reportService).findById(1L);
        verifyNoMoreInteractions(reportService);
    }

    @Test
    void getReportPreviewReturnsJpegByteArrayResourceWhenFileExists() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3, 4};
        Files.write(UPLOAD_DIR.resolve("report.jpg"), bytes);
        when(reportService.findById(1L)).thenReturn(Optional.of(report("/files/report.jpg")));

        mockMvc.perform(get("/api/reports/{id}/preview", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"report.jpg\""))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, bytes.length))
                .andExpect(content().bytes(bytes));

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
            return Files.createTempDirectory("nursery-astana-preview-");
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
