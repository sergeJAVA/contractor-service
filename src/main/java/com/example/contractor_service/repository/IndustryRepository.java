package com.example.contractor_service.repository;

import com.example.contractor_service.model.Industry;
import com.example.contractor_service.util.RowMappers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для взаимодействия с таблицей "industry" в базе данных
 * посредством {@link JdbcTemplate}.
 *
 * @author sergeJAVA
 */
@Repository
public class IndustryRepository {

    private final JdbcTemplate jdbcTemplate;

    public IndustryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получает список всех индустрий.
     *
     * @return Список объектов {@link Industry}.
     */
    public List<Industry> findAll() {
        String sql = "SELECT id, name FROM industry ORDER BY name";
        return jdbcTemplate.query(sql, RowMappers.INDUSTRY_ROW_MAPPER);
    }

    /**
     * Получает индустрию по её уникальному идентификатору (ID).
     *
     * @param id ID индустрии.
     * @return {@link Optional} содержащий объект {@link Industry}, если найден, иначе пустой {@link Optional}.
     */
    public Optional<Industry> findById(int id) {
        String sql = "SELECT id, name FROM industry WHERE id = ?";
        List<Industry> industries = jdbcTemplate.query(sql, RowMappers.INDUSTRY_ROW_MAPPER, id);
        return industries.stream().findFirst();
    }

    /**
     * Сохраняет новую индустрию или обновляет существующую.
     *
     * @param industry Объект {@link Industry} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link Industry}.
     */
    public Industry save(Industry industry) {
        Optional<Industry> existingIndustry = findById(industry.getId());
        if (existingIndustry.isPresent()) {
            String sql = "UPDATE industry SET name = ? WHERE id = ?";
            jdbcTemplate.update(sql, industry.getName(), industry.getId());
        } else {
            String sql = "INSERT INTO industry (id, name) VALUES (?, ?)";
            jdbcTemplate.update(sql, industry.getId(), industry.getName());
        }
        return industry;
    }

    /**
     * Выполняет физическое удаление индустрии по её ID.
     *
     * @param id ID индустрии для удаления.
     * @return Количество затронутых строк.
     */
    public int delete(int id) {
        String sql = "DELETE FROM industry WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

}
