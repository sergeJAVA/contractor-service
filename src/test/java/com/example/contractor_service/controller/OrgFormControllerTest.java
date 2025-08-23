package com.example.contractor_service.controller;

import com.example.contractor_service.model.OrgForm;
import com.example.contractor_service.testcontainers.TestContainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class OrgFormControllerTest extends TestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Должен создать новую орг. форму и вернуть 200 OK")
    void shouldCreateNewOrgForm() throws Exception {
        OrgForm newOrgForm = new OrgForm(200, "Test");

        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrgForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    @DisplayName("Должен обновить существующую орг. форму")
    void shouldUpdateExistingOrgForm() throws Exception {
        OrgForm existingOrgForm = new OrgForm(201, "Общество Тестеров");
        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingOrgForm)))
                .andExpect(status().isOk());

        existingOrgForm.setName("НОВЫЙ ТЕСТ");
        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingOrgForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("НОВЫЙ ТЕСТ"));

        mockMvc.perform(get("/org_form/{id}", 201))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("НОВЫЙ ТЕСТ"));
    }

    @Test
    @DisplayName("Должен вернуть орг. форму по ID")
    void shouldReturnOrgFormById() throws Exception {
        OrgForm orgForm = new OrgForm(202, "Общество любителей тестов");
        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orgForm)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/org_form/{id}", 202))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(202))
                .andExpect(jsonPath("$.name").value("Общество любителей тестов"));
    }

    @Test
    @DisplayName("Должен вернуть 404, если орг. форма не найдена")
    void shouldReturnNotFoundIfOrgFormDoesNotExist() throws Exception {
        mockMvc.perform(get("/org_form/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Должен выполнить физическое удаление орг. формы")
    void shouldPerformPhysicalDeleteOfOrgForm() throws Exception {
        OrgForm orgForm = new OrgForm(345, "МегаТест");
        // Создали
        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orgForm)))
                .andExpect(status().isOk());
        // Нашли
        mockMvc.perform(get("/org_form/{id}", 345))
                .andExpect(status().isOk());
        // Удалили
        mockMvc.perform(delete("/org_form/delete/{id}", 345))
                .andExpect(status().isNoContent());
        // Пытаемся найти, но без успеха - так и надо
        mockMvc.perform(get("/org_form/{id}", 345))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Кэшированное получение всех организационных форм и сброс после изменения")
    void testOrgFormCaching() throws Exception {
        OrgForm orgForm = new OrgForm(104, "OOO");

        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orgForm)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/org_form/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("OOO"));

        assertThat(cacheManager.getCache("orgforms").get("all")).isNotNull();

        orgForm.setName("OOO Updated");
        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orgForm)))
                .andExpect(status().isOk());

        assertThat(cacheManager.getCache("orgforms").get("all")).isNull();
    }

}