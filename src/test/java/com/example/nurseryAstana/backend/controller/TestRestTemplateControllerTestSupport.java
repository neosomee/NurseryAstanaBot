package com.example.nurseryAstana.backend.controller;

import com.example.nurseryAstana.backend.adoption.service.AdoptionService;
import com.example.nurseryAstana.backend.animal.service.impl.AnimalServiceImple;
import com.example.nurseryAstana.backend.report.service.ReportService;
import com.example.nurseryAstana.backend.user.service.imple.UserServiceImple;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
                "telegram.bot.token=test-token",
                "app.upload-dir=${java.io.tmpdir}/nursery-astana-test-uploads"
        }
)
public abstract class TestRestTemplateControllerTestSupport {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected TelegramBot telegramBot;

    @MockitoBean
    protected AnimalServiceImple animalService;

    @MockitoBean
    protected AdoptionService adoptionService;

    @MockitoBean
    protected ReportService reportService;

    @MockitoBean
    protected UserServiceImple userService;

    @BeforeEach
    void resetMocks() {
        reset(telegramBot, animalService, adoptionService, reportService, userService);
    }

    protected static void assertJsonContentType(MediaType contentType) {
        assertThat(contentType).isNotNull();
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
    }
}
