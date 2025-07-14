package com.example.contractor_service.controller.ui;

import com.example.contractor_service.model.Industry;
import com.example.contractor_service.service.IndustryService;
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
@RequestMapping("/ui/industry")
@RequiredArgsConstructor
@Tag(name = "Industry с аутентификацией", description = "API для управления индустриями, требующий аутентификации")
public class UIIndustryController {

    private final IndustryService industryService;

    @Operation(summary = "Получить все индустрии", description = "Возвращает полный список всех индустрий.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список индустрий успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Industry.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Industry>> getAllIndustries() {
        List<Industry> industries = industryService.findAll();
        return ResponseEntity.ok(industries);
    }

    @Operation(summary = "Получить индустрию по ID", description = "Возвращает информацию об индустрии по ее уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Индустрия успешно найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Industry.class))),
            @ApiResponse(responseCode = "404", description = "Индустрия не найдена",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Industry> getIndustryById(@PathVariable int id) {
        return industryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Сохранить или обновить индустрию", description = "Создает новую индустрию или обновляет существующую. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Индустрия успешно сохранена/обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Industry.class)))
    })
    @PutMapping("/save")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Industry> saveIndustry(@RequestBody Industry industry) {
        Industry savedIndustry = industryService.save(industry);
        return ResponseEntity.ok(savedIndustry);
    }

    @Operation(summary = "Удалить индустрию", description = "Выполняет физическое удаление индустрии по ее идентификатору. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Индустрия успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Индустрия не найдена",
                    content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Void> deleteIndustry(@PathVariable int id) {
        int deletedRows = industryService.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
