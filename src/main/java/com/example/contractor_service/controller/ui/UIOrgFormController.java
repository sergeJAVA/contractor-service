package com.example.contractor_service.controller.ui;

import com.example.contractor_service.model.OrgForm;
import com.example.contractor_service.service.OrgFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ui/org_form")
@RequiredArgsConstructor
@Tag(name = "OrgForm с аутентификацией", description = "API для управления организационными формами, требующий аутентификации")
public class UIOrgFormController {

    private final OrgFormService orgFormService;

    @Operation(summary = "Получить все организационные формы", description = "Возвращает полный список всех организационных форм.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список организационных форм успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgForm.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<OrgForm>> getAllOrgForms() {
        List<OrgForm> orgForms = orgFormService.findAll();
        return ResponseEntity.ok(orgForms);
    }

    @Operation(summary = "Получить организационную форму по ID", description = "Возвращает информацию об организационной форме по ее уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Организационная форма успешно найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgForm.class))),
            @ApiResponse(responseCode = "404", description = "Организационная форма не найдена",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrgForm> getOrgFormById(@PathVariable int id) {
        return orgFormService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Сохранить или обновить организационную форму", description = "Создает новую организационную форму или обновляет существующую. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Организационная форма успешно сохранена/обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgForm.class)))
    })
    @PutMapping("/save")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<OrgForm> saveOrgForm(@RequestBody OrgForm orgForm) {
        OrgForm savedOrgForm = orgFormService.save(orgForm);
        return ResponseEntity.ok(savedOrgForm);
    }

    @Operation(summary = "Удалить организационную форму", description = "Выполняет физическое удаление организационной формы по ее идентификатору. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Организационная форма успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Организационная форма не найдена",
                    content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Void> deleteOrgForm(@PathVariable int id) {
        int deletedRows = orgFormService.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
