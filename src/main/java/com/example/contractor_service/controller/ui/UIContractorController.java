package com.example.contractor_service.controller.ui;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.SearchRequest;
import com.example.contractor_service.model.SearchResponse;
import com.example.contractor_service.model.security.TokenAuthentication;
import com.example.contractor_service.model.security.TokenData;
import com.example.contractor_service.service.ContractorService;
import com.example.contractor_service.service.outbox.OutboxMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ui/contractor")
@RequiredArgsConstructor
@Tag(name = "Contractor с аутентификацией", description = "API для управления контрагентами, требует аутентификации")
public class UIContractorController {

    private final ContractorService contractorService;
    private final OutboxMessageService outboxMessageService;

    @Operation(summary = "Получить всех контрагентов",
            description = "Возвращает список всех активных контрагентов со всей связанной информацией")
    @ApiResponse(responseCode = "200", description = "Все контрагенты успешно получены",
            content = @Content(mediaType = "application/json"))
    @GetMapping("/all")
    public ResponseEntity<List<Contractor>> findAll() {
        return ResponseEntity.ok(contractorService.findAll());
    }

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
            " обновляет существующего. Служебные поля управляются системой. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Контрагент успешно создан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contractor.class))),
            @ApiResponse(responseCode = "200", description = "Контрагент успешно обновлен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contractor.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос (например, отсутствует ID)",
                    content = @Content)
    })
    @PutMapping("/save")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Contractor> saveContractor(@RequestBody Contractor contractor,
                                                     Authentication authentication) {

        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();


        if (contractor.getId() == null || contractor.getId().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // ID должен быть предоставлен
        }

        Optional<Contractor> existingContractor = contractorService.findById(contractor.getId());

        Contractor savedContractor = contractorService.save(contractor, tokenData.getId());

        Optional<Contractor> updated = contractorService.findById(savedContractor.getId());
        updated.ifPresent(outboxMessageService::saveContractor);

        if (existingContractor.isEmpty()) {
            return new ResponseEntity<>(savedContractor, HttpStatus.CREATED);
        } else {
            return ResponseEntity.ok(savedContractor);
        }
    }

    @Operation(summary = "Логически удалить контрагента", description = "Устанавливает флаг 'is_active' контрагента в FALSE, сохраняя запись в базе данных. " +
        "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Контрагент успешно удален (логически)"),
            @ApiResponse(responseCode = "404", description = "Контрагент не найден",
                    content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Void> deleteContractor(@PathVariable String id) {
        int deletedRows = contractorService.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Поиск контрагентов", description = "Ищет активных контрагентов по заданным фильтрам с поддержкой пагинации. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER, CONTRACTOR_RUS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список контрагентов успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponse.class)))
    })
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER', 'CONTRACTOR_RUS')")
    public ResponseEntity<SearchResponse> searchContractors(@RequestBody SearchRequest request,
                                                            Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();

        List<String> roles = tokenData.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        int page = Math.max(0, request.getPage());
        int size = Math.max(1, request.getSize());

        Map<String, String> filters = request.getFilters() != null ? request.getFilters() : new HashMap<>();

        if (roles.contains("SUPERUSER") || roles.contains("CONTRACTOR_SUPERUSER")) {
            List<Contractor> contractors = contractorService.search(filters, page, size);
            int totalElements = contractorService.countSearch(filters);

            return ResponseEntity.ok(new SearchResponse(contractors, page, size, totalElements));
        }

        // Проверка для CONTRACTOR_RUS
        if (filters.size() != 1 || !("RUS".equals(filters.get("countryId")))) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Contractor> contractors = contractorService.search(filters, page, size);
        int totalElements = contractorService.countSearch(filters);

        return ResponseEntity.ok(new SearchResponse(contractors, page, size, totalElements));
    }

}
