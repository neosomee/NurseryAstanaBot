package com.example.nurseryAstana.backend.adoption.controller;

import com.example.nurseryAstana.backend.adoption.dto.AdoptionResponse;
import com.example.nurseryAstana.backend.adoption.dto.CreateAdoptionRequest;
import com.example.nurseryAstana.backend.adoption.model.AdoptionStatus;
import com.example.nurseryAstana.backend.adoption.service.imple.AdoptionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdoptionController.class)
class AdoptionControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdoptionServiceImpl adoptionService;

    @Test
    void findAllAdoptReturnsAdoptions() throws Exception {
        when(adoptionService.findAllAdopt()).thenReturn(List.of(adoption(1L, AdoptionStatus.TRIAL), adoption(2L, AdoptionStatus.SUCCESS)));

        mockMvc.perform(get("/adoptions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].animalId").value(10L))
                .andExpect(jsonPath("$[0].userId").value(20L))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists())
                .andExpect(jsonPath("$[0].status").value("TRIAL"))
                .andExpect(jsonPath("$[1].status").value("SUCCESS"));

        verify(adoptionService).findAllAdopt();
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void getAllAdoptionReturnsAdoptionWhenPresent() throws Exception {
        when(adoptionService.findAdoptById(1L)).thenReturn(Optional.of(adoption(1L, AdoptionStatus.TRIAL)));

        mockMvc.perform(get("/adoptions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("TRIAL"));

        verify(adoptionService).findAdoptById(1L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void getAllAdoptionReturnsNotFoundWhenMissing() throws Exception {
        when(adoptionService.findAdoptById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/adoptions/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(adoptionService).findAdoptById(99L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void createAdoptionCreatesAdoption() throws Exception {
        when(adoptionService.createAdoption(any(CreateAdoptionRequest.class))).thenReturn(adoption(1L, AdoptionStatus.TRIAL));

        mockMvc.perform(post("/adoptions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAdoptionRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.animalId").value(10L))
                .andExpect(jsonPath("$.userId").value(20L));

        verify(adoptionService).createAdoption(argThat(request ->
                request.getAnimalId().equals(10L) && request.getUserId().equals(20L)));
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void removeAdoptionRemovesById() throws Exception {
        mockMvc.perform(delete("/adoptions/remove/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(adoptionService).removeAdoptById(1L);
        verifyNoMoreInteractions(adoptionService);
    }

    @Test
    void trialdaysSuccessFinishesTrial() throws Exception {
        when(adoptionService.finishTrial(1L)).thenReturn(Optional.of(adoption(1L, AdoptionStatus.SUCCESS)));

        mockMvc.perform(put("/adoptions/trialdays/success/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(adoptionService).finishTrial(1L);
        verifyNoMoreInteractions(adoptionService);
    }


    @Test
    void trialdaysExtendReturnsNotFoundWhenMissing() throws Exception {
        when(adoptionService.extendTrial(99L, 5)).thenReturn(Optional.empty());

        mockMvc.perform(put("/adoptions/trialdays/extend/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("5"))
                .andExpect(status().isNotFound());

        verify(adoptionService).extendTrial(99L, 5);
        verifyNoMoreInteractions(adoptionService);
    }

    private static AdoptionResponse adoption(Long id, AdoptionStatus status) {
        AdoptionResponse response = new AdoptionResponse();
        response.setId(id);
        response.setAnimalId(10L);
        response.setUserId(20L);
        response.setStartDate(LocalDateTime.of(2026, 5, 1, 10, 0));
        response.setEndDate(LocalDateTime.of(2026, 5, 15, 10, 0));
        response.setStatus(status);
        return response;
    }

    private static CreateAdoptionRequest createAdoptionRequest() {
        CreateAdoptionRequest request = new CreateAdoptionRequest();
        request.setAnimalId(10L);
        request.setUserId(20L);
        return request;
    }
}
