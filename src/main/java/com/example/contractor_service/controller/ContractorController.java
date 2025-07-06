package com.example.contractor_service.controller;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.SearchRequest;
import com.example.contractor_service.model.SearchResponse;
import com.example.contractor_service.service.ContractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

/**
 * REST контроллер для управления сущностью "contractor".
 * Обрабатывает запросы на создание, обновление, получение,
 * логическое удаление и поиск контрагентов с пагинацией.
 *
 * @author sergeJAVA
 */
@RestController
@RequestMapping("/contractor")
@RequiredArgsConstructor
@Tag(name = "Contractor", description = "API для управления контрагентами")
public class ContractorController {

    private final ContractorService contractorService;

    @Operation(summary = "Получить контрагента по ID", description = "Возвращает информацию о контрагенте по его уникальному идентификатору, включая связанные данные.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Контрагент успешно найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contractor.class))),
            @ApiResponse(responseCode = "404", description = "Контрагент не найден",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Contractor> getContractorById(@PathVariable String id) {
        return contractorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Сохранить или обновить контрагента", description = "Создает нового контрагента или" +
            " обновляет существующего. Служебные поля управляются системой.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Контрагент успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contractor.class))),
            @ApiResponse(responseCode = "200", description = "Контрагент успешно обновлен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contractor.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос (например, отсутствует ID)",
                    content = @Content)
    })
    @PutMapping("/save")
    public ResponseEntity<Contractor> saveContractor(@RequestBody Contractor contractor) {
        if (contractor.getId() == null || contractor.getId().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // ID должен быть предоставлен
        }

        Optional<Contractor> existingContractor = contractorService.findById(contractor.getId());

        Contractor savedContractor = contractorService.save(contractor);

        if (existingContractor.isEmpty()) {
            return new ResponseEntity<>(savedContractor, HttpStatus.CREATED);
        } else {
            return ResponseEntity.ok(savedContractor);
        }
    }

    @Operation(summary = "Логически удалить контрагента", description = "Устанавливает флаг 'is_active' контрагента в FALSE, сохраняя запись в базе данных.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Контрагент успешно удален (логически)"),
            @ApiResponse(responseCode = "404", description = "Контрагент не найден",
                    content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContractor(@PathVariable String id) {
        int deletedRows = contractorService.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Поиск контрагентов", description = "Ищет активных контрагентов по заданным фильтрам с поддержкой пагинации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список контрагентов успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponse.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<SearchResponse> searchContractors(@RequestBody SearchRequest request) {
        // Убедимся, что page и size корректны
        int page = Math.max(0, request.getPage()); // Минимум 0
        int size = Math.max(1, request.getSize()); // Минимум 1

        List<Contractor> contractors = contractorService.search(request.getFilters(), page, size);
        int totalElements = contractorService.countSearch(request.getFilters());

        SearchResponse response = new SearchResponse(contractors, page, size, totalElements);
        return ResponseEntity.ok(response);
    }

}
