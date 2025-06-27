package com.example.contractor_service.repository;

import com.example.contractor_service.model.Country;
import com.example.contractor_service.util.RowMappers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для взаимодействия с таблицей "country" в базе данных
 * посредством {@link JdbcTemplate}.
 *
 * @author sergeJAVA
 */
@Repository
public class CountryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CountryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получает список всех стран.
     *
     * @return Список объектов {@link Country}.
     */
    public List<Country> findAll() {
        String sql = "SELECT id, name FROM country ORDER BY name";
        return jdbcTemplate.query(sql, RowMappers.COUNTRY_ROW_MAPPER);
    }

    /**
     * Получает страну по её уникальному идентификатору (ID).
     *
     * @param id ID страны.
     * @return {@link Optional} содержащий объект {@link Country}, если найден, иначе пустой {@link Optional}.
     */
    public Optional<Country> findById(String id) {
        String sql = "SELECT id, name FROM country WHERE id = ?";
        List<Country> countries = jdbcTemplate.query(sql, RowMappers.COUNTRY_ROW_MAPPER, id);
        return countries.stream().findFirst();
    }

    /**
     * Сохраняет новую страну или обновляет существующую.
     * Если страна с таким ID существует, она обновляется; иначе - создается новая.
     *
     * @param country Объект {@link Country} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link Country}.
     */
    public Country save(Country country) {
        // Проверяем, существует ли страна с таким ID
        Optional<Country> existingCountry = findById(country.getId());
        if (existingCountry.isPresent()) {
            // Обновление существующей
            String sql = "UPDATE country SET name = ? WHERE id = ?";
            jdbcTemplate.update(sql, country.getName(), country.getId());
        } else {
            // Вставка новой
            String sql = "INSERT INTO country (id, name) VALUES (?, ?)";
            jdbcTemplate.update(sql, country.getId(), country.getName());
        }
        return country;
    }

    /**
     * Выполняет логическое удаление страны по её ID, устанавливая признак 'is_active' в false.
     *
     * @param id ID страны для логического удаления.
     * @return Количество затронутых строк (1, если успешно, 0, если не найдено).
     */
    public int delete(String id) {
        String sql = "UPDATE country SET is_active = FALSE WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

}
