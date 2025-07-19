package com.example.contractor_service.service;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.repository.ContractorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для управления операциями, связанными с контрагентами.
 * Предоставляет методы для поиска, сохранения, удаления и поиска контрагентов с фильтрацией и пагинацией.
 * Взаимодействует с {@link ContractorRepository} для доступа к данным.
 */
public interface ContractorService {

    /**
     * Находит контрагента по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор контрагента.
     * @return {@link Optional} содержащий объект {@link Contractor}, если найден, иначе пустой {@link Optional}.
     */
    Optional<Contractor> findById(String id);

    /**
     * Сохраняет нового контрагента или обновляет существующего.
     * Если контрагент с таким ID существует, он будет обновлен; в противном случае будет создан новый.
     *
     * @param contractor Объект {@link Contractor} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link Contractor}.
     */
    Contractor save(Contractor contractor);

    /**
     * Выполняет логическое удаление контрагента по его идентификатору.
     * Устанавливает признак активности (`is_active`) в `FALSE` в базе данных.
     *
     * @param id Уникальный идентификатор контрагента, который нужно удалить.
     * @return Количество затронутых строк (обычно 1, если удаление успешно, 0, если контрагент не найден).
     */
    int delete(String id);

    /**
     * Ищет контрагентов по заданным фильтрам с поддержкой пагинации.
     * Поиск осуществляется по различным полям контрагента и связанным сущностям (страна, индустрия, орг. форма).
     *
     * @param filters Карта, содержащая критерии фильтрации, где ключ - имя поля, значение - критерий поиска.
     * Поддерживаемые фильтры: "contractor_id", "parent_id", "contractor_search" (поиск по name, name_full, inn, ogrn),
     * "country", "industry", "org_form".
     * @param page Номер страницы для пагинации.
     * @param size Количество элементов на странице.
     * @return Список объектов {@link Contractor}, соответствующих критериям поиска.
     */
    List<Contractor> search(Map<String, String> filters, int page, int size);

    /**
     * Подсчитывает общее количество контрагентов, соответствующих заданным фильтрам.
     * Используется для определения общего количества страниц при пагинации.
     *
     * @param filters (Map), содержащая критерии фильтрации, где ключ - имя поля, значение - критерий поиска.
     * Использует те же фильтры, что и метод {@code search}.
     * @return Общее количество активных контрагентов, соответствующих фильтрам.
     */
    int countSearch(java.util.Map<String, String> filters);

    Contractor save(Contractor contractor, Long userId);

}
