package com.example.contractor_service.controller.ui;

import com.example.contractor_service.model.Country;
import com.example.contractor_service.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/ui/country")
@Tag(name = "Country с аутентификацией", description = "API для управления странами, требующий аутентификации")
public class UICountryController {

    private final CountryService service;

    public UICountryController(CountryService service) {
        this.service = service;
    }

    @Operation(summary = "Получить все страны", description = "Возвращает полный список всех стран.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список стран успешно возвращен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = service.findAll();
        return ResponseEntity.ok(countries);
    }

    @Operation(summary = "Получить страну по ID", description = "Возвращает информацию о стране по ее уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страна успешно найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class))),
            @ApiResponse(responseCode = "404", description = "Страна не найдена",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Сохранить или обновить страну", description = "Создает новую страну или обновляет существующую. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страна успешно сохранена/обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Country.class)))
    })
    @PutMapping("/save")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Country> saveCountry(@RequestBody Country country) {
        Country savedCountry = service.save(country);
        return ResponseEntity.ok(savedCountry);
    }

    @Operation(summary = "Логически удалить страну", description = "Устанавливает флаг 'is_active' страны в FALSE. " +
            "Требуемые роли: SUPERUSER, CONTRACTOR_SUPERUSER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Страна успешно удалена (логически)"),
            @ApiResponse(responseCode = "404", description = "Страна не найдена",
                    content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'CONTRACTOR_SUPERUSER')")
    public ResponseEntity<Void> deleteCountry(@PathVariable String id) {
        int deletedRows = service.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
