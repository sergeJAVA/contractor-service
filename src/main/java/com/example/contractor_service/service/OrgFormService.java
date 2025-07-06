package com.example.contractor_service.service;

import com.example.contractor_service.model.OrgForm;
import com.example.contractor_service.repository.OrgFormRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления операциями, связанными с организационными формами.
 * Предоставляет методы для получения списка всех организационных форм, поиска по ID, сохранения и удаления.
 * Взаимодействует с {@link OrgFormRepository} для доступа к данным.
 */
public interface OrgFormService {

    /**
     * Получает список всех организационных форм.
     *
     * @return Список объектов {@link OrgForm}.
     */
    List<OrgForm> findAll();

    /**
     * Находит организационную форму по её уникальному целочисленному идентификатору.
     *
     * @param id Уникальный целочисленный идентификатор организационной формы.
     * @return {@link Optional} содержащий объект {@link OrgForm}, если найден, иначе пустой {@link Optional}.
     */
    Optional<OrgForm> findById(int id);

    /**
     * Сохраняет новую организационную форму или обновляет существующую.
     * Если организационная форма с таким ID существует, она будет обновлена; в противном случае будет создана новая.
     *
     * @param orgForm Объект {@link OrgForm} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link OrgForm}.
     */
    OrgForm save(OrgForm orgForm);

    /**
     * Выполняет физическое удаление организационной формы по её идентификатору.
     *
     * @param id Уникальный целочисленный идентификатор организационной формы, которую нужно удалить.
     * @return Количество затронутых строк (обычно 1, если удаление успешно).
     */
    int delete(int id);

}
