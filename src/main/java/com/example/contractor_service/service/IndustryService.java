package com.example.contractor_service.service;

import com.example.contractor_service.model.Industry;
import com.example.contractor_service.repository.IndustryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления операциями, связанными с индустриями.
 * Предоставляет методы для получения списка всех индустрий, поиска по ID, сохранения и удаления индустрий.
 * Взаимодействует с {@link IndustryRepository} для доступа к данным.
 */
public interface IndustryService {

    /**
     * Получает список всех индустрий.
     *
     * @return Список объектов {@link Industry}.
     */
    List<Industry> findAll();

    /**
     * Находит индустрию по её уникальному целочисленному идентификатору.
     *
     * @param id Уникальный целочисленный идентификатор индустрии.
     * @return {@link Optional} содержащий объект {@link Industry}, если найден, иначе пустой {@link Optional}.
     */
    Optional<Industry> findById(int id);

    /**
     * Сохраняет новую индустрию или обновляет существующую.
     * Если индустрия с таким ID существует, она будет обновлена; в противном случае будет создана новая.
     *
     * @param industry Объект {@link Industry} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link Industry}.
     */
    Industry save(Industry industry);

    /**
     * Выполняет физическое удаление индустрии по её идентификатору.
     *
     * @param id Уникальный целочисленный идентификатор индустрии, которую нужно удалить.
     * @return Количество затронутых строк (обычно 1, если удаление успешно).
     */
    int delete(int id);

}
