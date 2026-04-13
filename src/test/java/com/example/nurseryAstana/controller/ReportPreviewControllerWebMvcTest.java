package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.model.Report;
import com.example.nurseryAstana.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportPreviewController.class)
class ReportPreviewControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    void getReportPreview_returnsImage_whenFileExists() throws Exception {
        Long reportId = 1L;
        String photoUrl = "/files/test-image.jpg";

        Report report = new Report();
        report.setId(reportId);
        report.setPhotoUrl(photoUrl);

        when(reportService.findById(reportId)).thenReturn(Optional.of(report));

        Path uploadDir = Paths.get("uploads/reports").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        Path filePath =  uploadDir.resolve("test-image.jpg");
        byte[] fileContent = "fake-image-content".getBytes();
        Files.write(filePath, fileContent);

        mockMvc.perform(get("/api/reports/{id}/preview", reportId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"test-image.jpg\""))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    void getReportPreview_returns404_whenPhotoUrlIsNull() throws Exception {
        Long reportId = 2L;
        Report report = new Report();
        report.setId(reportId);
        report.setPhotoUrl(null);

        when(reportService.findById(reportId)).thenReturn(Optional.of(report));

        mockMvc.perform(get("/api/reports/{id}/preview", reportId))
                .andExpect(status().isNotFound());
    }

}
