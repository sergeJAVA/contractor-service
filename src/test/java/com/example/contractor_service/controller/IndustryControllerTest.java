package com.example.contractor_service.controller;

import com.example.contractor_service.model.Industry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class IndustryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен создать новую отрасль и вернуть 200 OK")
    void shouldCreateNewIndustry() throws Exception {
        Industry newIndustry = new Industry(200, "Тест");

        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIndustry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.name").value("Тест"));
    }

    @Test
    @DisplayName("Должен обновить существующую отрасль")
    void shouldUpdateExistingIndustry() throws Exception {
        // Создаем отрасль
        Industry existingIndustry = new Industry(101, "Производство");
        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingIndustry)))
                .andExpect(status().isOk());

        // Обновляем ее
        existingIndustry.setName("Тестовое производство");
        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingIndustry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовое производство"));

        mockMvc.perform(get("/industry/{id}", 101))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тестовое производство"));
    }

    @Test
    @DisplayName("Должен вернуть отрасль по ID")
    void shouldReturnIndustryById() throws Exception {
        Industry industry = new Industry(102, "Тестовая торговля");
        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(industry)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/industry/{id}", 102))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(102))
                .andExpect(jsonPath("$.name").value("Тестовая торговля"));
    }

    @Test
    @DisplayName("Должен вернуть 404, если отрасль не найдена")
    void shouldReturnNotFoundIfIndustryDoesNotExist() throws Exception {
        mockMvc.perform(get("/industry/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Должен выполнить физическое удаление отрасли")
    void shouldPerformPhysicalDeleteOfIndustry() throws Exception {
        Industry industry = new Industry(105, "Отрасль для удаления");
        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(industry)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/industry/delete/{id}", 105))
                .andExpect(status().isNoContent());

        // Проверяем, что отрасль полностью удалена
        mockMvc.perform(get("/industry/{id}", 105))
                .andExpect(status().isNotFound());
    }

}