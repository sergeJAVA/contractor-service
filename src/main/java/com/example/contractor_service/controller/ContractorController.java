package com.example.contractor_service.controller;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.SearchRequest;
import com.example.contractor_service.model.SearchResponse;
import com.example.contractor_service.repository.ContractorRepository;
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
public class ContractorController {

    private final ContractorRepository contractorRepository;

    /**
     * Получает контрагента по его ID со всей связанной информацией.
     * GET /contractor/{id}
     *
     * @param id ID контрагента.
     * @return {@link ResponseEntity} с объектом {@link Contractor} (со связанными данными),
     * если найден, иначе 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contractor> getContractorById(@PathVariable String id) {
        return contractorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Сохраняет нового контрагента или обновляет существующего.
     * PUT /contractor/save
     * Служебные поля (create_date, modify_date, create_user_id, modify_user_id, is_active)
     * не должны передаваться извне; они управляются бэкендом.
     *
     * @param contractor Объект {@link Contractor} для сохранения или обновления.
     * @return {@link ResponseEntity} с сохраненным объектом {@link Contractor}.
     */
    @PutMapping("/save")
    public ResponseEntity<Contractor> saveContractor(@RequestBody Contractor contractor) {
        // ID должен быть известен (для создания или обновления).
        if (contractor.getId() == null || contractor.getId().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // ID должен быть предоставлен
        }

        // Проверяем, существует ли уже контрагент с таким ID
        Optional<Contractor> existingContractor = contractorRepository.findById(contractor.getId());

        Contractor savedContractor = contractorRepository.save(contractor);

        // Возвращаем статус 201 Created для новых записей и 200 OK для обновлений
        if (existingContractor.isEmpty()) {
            return new ResponseEntity<>(savedContractor, HttpStatus.CREATED);
        } else {
            return ResponseEntity.ok(savedContractor);
        }
    }

    /**
     * Выполняет логическое удаление контрагента, устанавливая `is_active` в `FALSE`.
     * DELETE /contractor/delete/{id}
     *
     * @param id ID контрагента для логического удаления.
     * @return {@link ResponseEntity} со статусом 204 No Content, если удаление успешно.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContractor(@PathVariable String id) {
        int deletedRows = contractorRepository.delete(id);
        if (deletedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Выполняет поиск активных контрагентов с возможностью фильтрации и пагинации.
     * POST /contractor/search
     *
     * @param request Объект {@link SearchRequest}, содержащий фильтры, номер страницы и размер страницы.
     * @return {@link ResponseEntity} с объектом {@link SearchResponse}, содержащим список контрагентов
     * и информацию о пагинации.
     */
    @PostMapping("/search")
    public ResponseEntity<SearchResponse> searchContractors(@RequestBody SearchRequest request) {
        // Убедимся, что page и size корректны
        int page = Math.max(0, request.getPage()); // Минимум 0
        int size = Math.max(1, request.getSize()); // Минимум 1

        List<Contractor> contractors = contractorRepository.search(request.getFilters(), page, size);
        int totalElements = contractorRepository.countSearch(request.getFilters());

        SearchResponse response = new SearchResponse(contractors, page, size, totalElements);
        return ResponseEntity.ok(response);
    }

}
