package com.example.nurseryAstana.controller;

import com.example.nurseryAstana.model.Report;
import com.example.nurseryAstana.model.User;
import com.example.nurseryAstana.service.ReportService;
import com.example.nurseryAstana.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

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
    private UserService userService;

    @MockitoBean
    private ReportService reportService;

    @Test
    void getAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("User user");

        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("User user"));
    }

    @Test
    void getAllReports_returnsOkAndJson() throws Exception {
        Report report = new Report();
        report.setId(10L);
        report.setText("test text");
        report.setPhotoUrl("http://example.com/photo.jpg");
        report.setCreatedAt(LocalDateTime.of(2026, 4, 13, 12, 0));

        when(reportService.findAllReports()).thenReturn(List.of(report));

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].text").value("test text"))
                .andExpect(jsonPath("$[0].photoUrl").value("http://example.com/photo.jpg"))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }
}