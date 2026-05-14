package com.example.nurseryAstana.backend.report.controller;

import com.example.nurseryAstana.backend.report.model.Report;
import com.example.nurseryAstana.backend.report.service.ReportService;
import com.example.nurseryAstana.backend.user.model.User;
import com.example.nurseryAstana.backend.user.service.imple.UserServiceImple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NurseryAstanaController.class)
class NurseryAstanaControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private UserServiceImple userService;

    @Test
    void getAllUsersReturnsUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setTelegramId(12345L);
        user.setUsername("alice");

        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].telegramId").value(12345L))
                .andExpect(jsonPath("$[0].username").value("alice"));

        verify(userService).findAllUsers();
        verifyNoMoreInteractions(userService, reportService);
    }

    @Test
    void getAllReportsReturnsReports() throws Exception {
        Report report = new Report();
        report.setId(10L);
        report.setText("Need help");
        report.setPhotoUrl("/files/report.jpg");
        report.setCreatedAt(LocalDateTime.of(2026, 5, 14, 12, 0));

        when(reportService.findAllReports()).thenReturn(List.of(report));

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].text").value("Need help"))
                .andExpect(jsonPath("$[0].photoUrl").value("/files/report.jpg"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].user").doesNotExist());

        verify(reportService).findAllReports();
        verifyNoMoreInteractions(userService, reportService);
    }
}
