package com.example.contractor_service.controller;
import com.example.contractor_service.model.Country;
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
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен создать новую страну и вернуть 200 OK")
    void shouldCreateNewCountry() throws Exception {
        Country newCountry = new Country("TEST", "TestCountry");

        mockMvc.perform(put("/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCountry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("TEST"))
                .andExpect(jsonPath("$.name").value("TestCountry"));
    }

    @Test
    @DisplayName("Должен обновить существующую страну")
    void shouldUpdateExistingCountry() throws Exception {
        // Создаем страну
        Country existingCountry = new Country("TESTIK", "Test");
        mockMvc.perform(put("/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingCountry)))
                .andExpect(status().isOk());

        // Обновляем ее
        existingCountry.setName("Федеративная Республика Теста");
        mockMvc.perform(put("/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingCountry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Федеративная Республика Теста"));

        // Проверяем, что изменения применились
        mockMvc.perform(get("/country/{id}", "TESTIK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Федеративная Республика Теста"));
    }

    @Test
    @DisplayName("Должен вернуть страну по ID")
    void shouldReturnCountryById() throws Exception {
        Country country = new Country("FRA", "Франция");
        mockMvc.perform(put("/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(country)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/country/{id}", "FRA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("FRA"))
                .andExpect(jsonPath("$.name").value("Франция"));
    }

    @Test
    @DisplayName("Должен вернуть 404, если страна не найдена")
    void shouldReturnNotFoundIfCountryDoesNotExist() throws Exception {
        mockMvc.perform(get("/country/{id}", "NONEXIST"))
                .andExpect(status().isNotFound());
    }

}