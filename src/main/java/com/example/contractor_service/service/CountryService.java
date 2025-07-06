package com.example.contractor_service.service;

import com.example.contractor_service.model.Country;
import com.example.contractor_service.repository.CountryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления операциями, связанными со странами.
 * Предоставляет методы для получения списка всех стран, поиска по ID, сохранения и удаления стран.
 * Взаимодействует с {@link CountryRepository} для доступа к данным.
 */
public interface CountryService {

    /**
     * Получает список всех стран.
     *
     * @return Список объектов {@link Country}.
     */
    List<Country> findAll();

    /**
     * Находит страну по её уникальному идентификатору.
     *
     * @param id Уникальный идентификатор страны.
     * @return {@link Optional} содержащий объект {@link Country}, если найден, иначе пустой {@link Optional}.
     */
    Optional<Country> findById(String id);

    /**
     * Сохраняет новую страну или обновляет существующую.
     * Если страна с таким ID существует, она будет обновлена; в противном случае будет создана новая.
     *
     * @param country Объект {@link Country} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link Country}.
     */
    Country save(Country country);

    /**
     * Выполняет логическое удаление страны по её идентификатору.
     * Устанавливает признак активности (`is_active`) в `FALSE` в базе данных.
     *
     * @param id Уникальный идентификатор страны, которую нужно удалить.
     * @return Количество затронутых строк (обычно 1, если удаление успешно, 0, если страна не найдена).
     */
    int delete(String id);

}
