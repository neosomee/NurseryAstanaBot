//package com.example.nurseryAstana.controller;
//
//
//import com.example.nurseryAstana.backend.report.model.Report;
//import com.example.nurseryAstana.service.ReportService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.*;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class ReportPreviewControllerRestTemplateTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @MockitoBean
//    private ReportService reportService;
//
//    private String baseUrl;
//
//    @BeforeEach
//    void setUp() {
//        baseUrl = "http://localhost:" + port;
//    }
//
//    @Test
//    void getReportPreview_returnsImageBytesAndHeaders() throws Exception {
//        Long reportId = 1L;
//        String photoUrl = "/files/test-image.jpg";
//
//        Report report = new Report();
//        report.setId(reportId);
//        report.setPhotoUrl(photoUrl);
//        when(reportService.findById(reportId)).thenReturn(Optional.of(report));
//
//        Path uploadDir = Paths.get("upload/reports").toAbsolutePath().normalize();
//        Files.createDirectories(uploadDir);
//        Path filePath = uploadDir.resolve("test-image.jpg");
//        byte[] expectedBytes = "fake-image.jpg".getBytes();
//        Files.write(filePath, expectedBytes);
//
//        ResponseEntity<byte[]> response = restTemplate.getForEntity(baseUrl + "api/reports/{id}/preview", byte[].class, reportId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
//        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
//                .isEqualTo("inline; filename=\"test-image.jpg\"");
//        assertThat(response.getBody()).isEqualTo(expectedBytes);
//    }
//
//    @Test
//    void getReportPreview_returns404_whenReportNotFound() {
//        Long reportId = 100L;
//        when(reportService.findById(reportId)).thenReturn(Optional.empty());
//
//        ResponseEntity<byte[]> response =
//                restTemplate.getForEntity(baseUrl + "/api/reports/{id}/preview", byte[].class, reportId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    void getReportPreview_returns404_whenPhotoUrlNull() {
//        Long reportId = 2L;
//        Report report = new Report();
//        report.setId(reportId);
//        report.setPhotoUrl(null);
//        when(reportService.findById(reportId)).thenReturn(Optional.of(report));
//
//        ResponseEntity<byte[]> response =
//                restTemplate.getForEntity(baseUrl + "/api/reports/{id}/preview", byte[].class, reportId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//}
