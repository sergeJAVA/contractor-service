package com.example.contractor_service.repository;

import com.example.contractor_service.model.OrgForm;
import com.example.contractor_service.util.RowMappers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для взаимодействия с таблицей "org_form" в базе данных
 * посредством {@link JdbcTemplate}.
 *
 * @author sergeJAVA
 */
@Repository
public class OrgFormRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrgFormRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получает список всех организационных форм.
     *
     * @return Список объектов {@link OrgForm}.
     */
    public List<OrgForm> findAll() {
        String sql = "SELECT id, name FROM org_form ORDER BY name";
        return jdbcTemplate.query(sql, RowMappers.ORG_FORM_ROW_MAPPER);
    }

    /**
     * Получает организационную форму по её уникальному идентификатору (ID).
     *
     * @param id ID организационной формы.
     * @return {@link Optional} содержащий объект {@link OrgForm}, если найден, иначе пустой {@link Optional}.
     */
    public Optional<OrgForm> findById(int id) {
        String sql = "SELECT id, name FROM org_form WHERE id = ?";
        List<OrgForm> orgForms = jdbcTemplate.query(sql, RowMappers.ORG_FORM_ROW_MAPPER, id);
        return orgForms.stream().findFirst();
    }

    /**
     * Сохраняет новую организационную форму или обновляет существующую.
     *
     * @param orgForm Объект {@link OrgForm} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link OrgForm}.
     */
    public OrgForm save(OrgForm orgForm) {
        Optional<OrgForm> existingOrgForm = findById(orgForm.getId());
        if (existingOrgForm.isPresent()) {
            String sql = "UPDATE org_form SET name = ? WHERE id = ?";
            jdbcTemplate.update(sql, orgForm.getName(), orgForm.getId());
        } else {
            String sql = "INSERT INTO org_form (id, name) VALUES (?, ?)";
            jdbcTemplate.update(sql, orgForm.getId(), orgForm.getName());
        }
        return orgForm;
    }

    /**
     * Выполняет физическое удаление организационной формы по её ID.
     *
     * @param id ID организационной формы для удаления.
     * @return Количество затронутых строк.
     */
    public int delete(int id) {
        String sql = "DELETE FROM org_form WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

}
