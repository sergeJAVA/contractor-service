package com.example.contractor_service.controller;

import com.example.contractor_service.model.OrgForm;
import com.example.contractor_service.repository.OrgFormRepository;
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
 * REST контроллер для управления справочником "org_form".
 *
 * @author sergeJAVA
 */
@RestController
@RequestMapping("/org_form")
@RequiredArgsConstructor
public class OrgFormController {

    private final OrgFormRepository orgFormRepository;

    /**
     * Получает список всех организационных форм.
     * GET /org_form/all
     *
     * @return {@link ResponseEntity} со списком объектов {@link OrgForm}.
     */
    @GetMapping("/all")
    public ResponseEntity<List<OrgForm>> getAllOrgForms() {
        List<OrgForm> orgForms = orgFormRepository.findAll();
        return ResponseEntity.ok(orgForms);
    }

    /**
     * Получает организационную форму по её ID.
     * GET /org_form/{id}
     *
     * @param id ID организационной формы.
     * @return {@link ResponseEntity} с объектом {@link OrgForm}, если найден, иначе 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrgForm> getOrgFormById(@PathVariable int id) {
        return orgFormRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Сохраняет новую организационную форму или обновляет существующую.
     * PUT /org_form/save
     *
     * @param orgForm Объект {@link OrgForm} для сохранения.
     * @return {@link ResponseEntity} с сохраненным объектом {@link OrgForm}.
     */
    @PutMapping("/save")
    public ResponseEntity<OrgForm> saveOrgForm(@RequestBody OrgForm orgForm) {
        OrgForm savedOrgForm = orgFormRepository.save(orgForm);
        return ResponseEntity.ok(savedOrgForm);
    }

    /**
     * Выполняет физическое удаление организационной формы по её ID.
     * DELETE /org_form/delete/{id}
     *
     * @param id ID организационной формы для удаления.
     * @return {@link ResponseEntity} со статусом 204 No Content, если удаление успешно.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrgForm(@PathVariable int id) {
        int deletedRows = orgFormRepository.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
