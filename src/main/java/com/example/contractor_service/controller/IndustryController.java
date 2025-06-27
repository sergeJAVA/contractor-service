package com.example.contractor_service.controller;

import com.example.contractor_service.model.Industry;
import com.example.contractor_service.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

/**
 * REST контроллер для управления справочником "industry".
 *
 * @author sergeJAVA
 */
@RestController
@RequestMapping("/industry")
@RequiredArgsConstructor
public class IndustryController {

    private final IndustryRepository industryRepository;

    /**
     * Получает список всех индустрий.
     * GET /industry/all
     *
     * @return {@link ResponseEntity} со списком объектов {@link Industry}.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Industry>> getAllIndustries() {
        List<Industry> industries = industryRepository.findAll();
        return ResponseEntity.ok(industries);
    }

    /**
     * Получает индустрию по её ID.
     * GET /industry/{id}
     *
     * @param id ID индустрии.
     * @return {@link ResponseEntity} с объектом {@link Industry}, если найден, иначе 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Industry> getIndustryById(@PathVariable int id) {
        return industryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Сохраняет новую индустрию или обновляет существующую.
     * PUT /industry/save
     *
     * @param industry Объект {@link Industry} для сохранения.
     * @return {@link ResponseEntity} с сохраненным объектом {@link Industry}.
     */
    @PutMapping("/save")
    public ResponseEntity<Industry> saveIndustry(@RequestBody Industry industry) {
        Industry savedIndustry = industryRepository.save(industry);
        return ResponseEntity.ok(savedIndustry);
    }

    /**
     * Выполняет физическое удаление индустрии по её ID.
     * DELETE /industry/delete/{id}
     *
     * @param id ID индустрии для удаления.
     * @return {@link ResponseEntity} со статусом 204 No Content, если удаление успешно.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteIndustry(@PathVariable int id) {
        int deletedRows = industryRepository.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
