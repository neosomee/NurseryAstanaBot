package com.example.nurseryAstana.backend.report.controller;

import com.example.nurseryAstana.backend.report.model.Report;
import com.example.nurseryAstana.backend.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * REST контроллер для предпросмотра фото репортов.
 * <p>
 * Предоставляет endpoint для получения изображения, связанного с репортом,
 * в виде бинарного HTTP-ответа. Удобно использовать в Swagger UI для визуального
 * просмотра прикреплённых фотографий.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report Preview", description = "Предпросмотр фото репортов")
public class ReportPreviewController {

    private final ReportService reportService;

    @Value("${app.upload.reports-dir:uploads/reports}")
    private String reportsUploadDir;

    /**
     * Возвращает изображение, прикреплённое к репорту.
     * <p>
     * Логика работы:
     * <ol>
     *     <li>По идентификатору репорта считывается сущность из базы данных.</li>
     *     <li>Из поля {@code photoUrl} извлекается имя файла, сохранённого в файловой системе.</li>
     *     <li>Файл читается с диска и отдаётся как бинарный поток с типом {@code image/jpeg}.</li>
     * </ol>
     * В случае отсутствия репорта или файла возвращается HTTP статус 404.
     *
     * @param id идентификатор репорта
     * @return {@link ResponseEntity} с изображением в теле ответа или статусом 404/500
     */
    @GetMapping("/{id}/preview")
    @Operation(summary = "Получить превью фото репорта по id")
    public ResponseEntity<ByteArrayResource> getReportPreview(@PathVariable Long id) {
        Optional<Report> optionalReport = reportService.findById(id);
        if (optionalReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Report report = optionalReport.get();
        if (report.getPhotoUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String photoUrl = report.getPhotoUrl();
            String fileName = photoUrl.replaceFirst("^/files/", "");

            Path uploadDir = Paths.get(reportsUploadDir).toAbsolutePath().normalize();
            Path filePath = uploadDir.resolve(fileName);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] bytes = Files.readAllBytes(filePath);
            ByteArrayResource resource = new ByteArrayResource(bytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + filePath.getFileName() + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .contentLength(bytes.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}