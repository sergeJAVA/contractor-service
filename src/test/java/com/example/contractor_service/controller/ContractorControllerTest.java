package com.example.contractor_service.controller;

import com.example.contractor_service.model.*;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Перезагружаем контекст перед каждым методом
public class ContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен создать нового контрагента и вернуть 201 CREATED")
    void shouldCreateNewContractor() throws Exception {
        Contractor newContractor = new Contractor();
        newContractor.setId("NEW_CONTR");
        newContractor.setName("Новый Контрагент");
        newContractor.setNameFull("Полное наименование нового контрагента");
        newContractor.setInn("1111111111");
        newContractor.setOgrn("2222222222");
        newContractor.setCountryId("RUS");
        newContractor.setIndustryId(1);
        newContractor.setOrgFormId(1);

        mockMvc.perform(put("/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newContractor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("NEW_CONTR"))
                .andExpect(jsonPath("$.name").value("Новый Контрагент"));
    }

    @Test
    @DisplayName("Должен обновить существующего контрагента и вернуть 200 OK")
    void shouldUpdateExistingContractor() throws Exception {
        Contractor existingContractor = new Contractor();
        existingContractor.setId("EXISTING");
        existingContractor.setName("Существующий Контрагент");
        existingContractor.setNameFull("Полное наименование");
        existingContractor.setInn("1234567890");
        existingContractor.setOgrn("0123456789");
        existingContractor.setCountryId("RUS");
        existingContractor.setIndustryId(1);
        existingContractor.setOrgFormId(1);

        mockMvc.perform(put("/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingContractor)))
                .andExpect(status().isCreated());


        existingContractor.setName("Обновленный Контрагент");
        mockMvc.perform(put("/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Country("ABH", "Абхазия"))))
                .andExpect(status().isOk());
        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Industry(25, "Строительство"))))
                .andExpect(status().isOk());
        mockMvc.perform(put("/org_form/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OrgForm(14, "АО"))))
                .andExpect(status().isOk());

        existingContractor.setCountryId("ABH");
        existingContractor.setIndustryId(25);
        existingContractor.setOrgFormId(14);

        mockMvc.perform(put("/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingContractor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("EXISTING"))
                .andExpect(jsonPath("$.name").value("Обновленный Контрагент"));

        mockMvc.perform(get("/contractor/{id}", "EXISTING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный Контрагент"))
                .andExpect(jsonPath("$.countryName").value("Абхазия"))
                .andExpect(jsonPath("$.industryName").value("Строительство"))
                .andExpect(jsonPath("$.orgFormName").value("АО"));
    }

    @Test
    @DisplayName("Должен вернуть контрагента по ID")
    void shouldReturnContractorById() throws Exception {
        Contractor contractor = new Contractor();
        contractor.setId("TEST_ID_1");
        contractor.setName("Test Contractor");
        contractor.setInn("1234567890");
        contractor.setCountryId("RUS");
        contractor.setIndustryId(1);
        contractor.setOrgFormId(1);
        mockMvc.perform(put("/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractor)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/contractor/{id}", "TEST_ID_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("TEST_ID_1"))
                .andExpect(jsonPath("$.name").value("Test Contractor"))
                .andExpect(jsonPath("$.countryName").value("Российская Федерация"));
    }

    @Test
    @DisplayName("Должен вернуть 404 Not Found, если контрагент не найден")
    void shouldReturnNotFoundIfContractorDoesNotExist() throws Exception {
        mockMvc.perform(get("/contractor/{id}", "NON_EXISTENT_ID"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Должен выполнить логическое удаление контрагента")
    void shouldPerformLogicalDeleteOfContractor() throws Exception {
        Contractor contractor = new Contractor();
        contractor.setId("TO_DELETE");
        contractor.setName("Contractor to be deleted");
        contractor.setInn("9876543210");
        contractor.setCountryId("RUS");
        contractor.setIndustryId(1);
        contractor.setOrgFormId(1);
        mockMvc.perform(put("/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractor)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/contractor/delete/{id}", "TO_DELETE"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/contractor/{id}", "TO_DELETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("TO_DELETE"))
                .andExpect(jsonPath("$.isActive").value(false));

        SearchRequest searchRequest = new SearchRequest();
        Map<String, String> filters = new HashMap<>();
        filters.put("contractor_id", "TO_DELETE");
        searchRequest.setFilters(filters);
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        MvcResult result = mockMvc.perform(post("/contractor/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andReturn();

        SearchResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), SearchResponse.class);
        assertThat(response.getContractors()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
    }

}