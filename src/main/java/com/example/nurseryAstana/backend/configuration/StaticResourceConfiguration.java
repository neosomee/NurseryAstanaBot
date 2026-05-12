package com.example.nurseryAstana.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Конфигурация раздачи статических ресурсов.
 * <p>
 * Настраивает маппинг URL вида {@code /files/**} на локальную директорию,
 * указанную в {@code app.upload-dir}. Это позволяет отдавать сохранённые
 * изображения репортов напрямую из файловой системы.
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * Регистрирует обработчик ресурсов для URL {@code /files/**}.
     * <p>
     * Все файлы из директории {@code app.upload-dir} становятся доступны
     * по пути {@code http://<host>:<port>/files/<имя-файла>}.
     *
     * @param registry реестр обработчиков ресурсов Spring MVC
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = "file:" + uploadPath.toString() + "/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);
    }
}