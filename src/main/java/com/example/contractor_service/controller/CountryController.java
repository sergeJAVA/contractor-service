package com.example.contractor_service.controller;

import com.example.contractor_service.model.Country;
import com.example.contractor_service.service.CountryService;
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
 * REST контроллер для управления справочником "country".
 *
 * @author sergeJAVA
 */
@RestController
@RequestMapping("/country")
public class CountryController {

    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    /**
     * Получает список всех стран.
     * GET /country/all
     *
     * @return {@link ResponseEntity} со списком объектов {@link Country}.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = service.findAll();
        return ResponseEntity.ok(countries);
    }

    /**
     * Получает страну по её ID.
     * GET /country/{id}
     *
     * @param id ID страны.
     * @return {@link ResponseEntity} с объектом {@link Country}, если найден, иначе 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Сохраняет новую страну или обновляет существующую.
     * PUT /country/save
     *
     * @param country Объект {@link Country} для сохранения.
     * @return {@link ResponseEntity} с сохраненным объектом {@link Country} и статусом 200 OK или 201 Created.
     */
    @PutMapping("/save")
    public ResponseEntity<Country> saveCountry(@RequestBody Country country) {
        // Здесь можно добавить логику проверки, существует ли страна,
        // чтобы вернуть 201 Created для новой и 200 OK для обновления
        // Для простоты, пока всегда возвращаем 200 OK
        Country savedCountry = service.save(country);
        return ResponseEntity.ok(savedCountry); // Или HttpStatus.CREATED для новых
    }

    /**
     * Выполняет логическое удаление страны по её ID.
     *
     * @param id ID страны для удаления.
     * @return {@link ResponseEntity} со статусом 204 No Content, если удаление успешно.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable String id) {
        int deletedRows = service.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found, если нечего удалять
        }
    }

}
