package com.example.nurseryAstana;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Поднимает весь контекст на настоящем PostgreSQL: проверяет, что миграции
 * Liquibase применяются и схема проходит {@code ddl-auto=validate}.
 * Без Docker тест пропускается.
 */
@SpringBootTest(properties = {
        "telegram.bot.token=test-token",
        "app.upload-dir=${java.io.tmpdir}/nursery-astana-test-uploads"
})
@Testcontainers(disabledWithoutDocker = true)
class NurseryAstanaApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@MockitoBean
	TelegramBot telegramBot;

	@Test
	void contextLoads() {
	}

}
