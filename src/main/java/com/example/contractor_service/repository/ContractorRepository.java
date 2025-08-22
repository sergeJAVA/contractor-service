package com.example.contractor_service.repository;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.util.RowMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для взаимодействия с таблицей "contractor" в базе данных
 * посредством {@link JdbcTemplate}.
 *
 * @author sergeJAVA
 */
@Repository
public class ContractorRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractorRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public ContractorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получает список всех контрагентов со всей связанной информацией.
     * @return список объектов {@link Contractor}.
     */
    public List<Contractor> findAll() {
        String sql = "SELECT c.id, c.parent_id, c.name, c.name_full, c.inn, c.ogrn, " +
                "c.country, c.industry, c.org_form, " +
                "c.create_date, c.modify_date, c.create_user_id, c.modify_user_id, c.is_active, " +
                "co.name AS country_name, i.name AS industry_name, o.name AS org_form_name " +
                "FROM contractor c " +
                "LEFT JOIN country co ON c.country = co.id " +
                "LEFT JOIN industry i ON c.industry = i.id " +
                "LEFT JOIN org_form o ON c.org_form = o.id " +
                "WHERE c.is_active = TRUE";

        return jdbcTemplate.query(sql, RowMappers.CONTRACTOR_ROW_MAPPER);
    }

    /**
     * Получает контрагента по его уникальному идентификатору (ID) со всей связанной информацией.
     * Использует JOIN для получения названий страны, индустрии и организационной формы.
     *
     * @param id ID контрагента.
     * @return {@link Optional} содержащий объект {@link Contractor}, если найден, иначе пустой {@link Optional}.
     */
    public Optional<Contractor> findById(String id) {
        String sql = "SELECT c.id, c.parent_id, c.name, c.name_full, c.inn, c.ogrn, " +
                "c.country, c.industry, c.org_form, " +
                "c.create_date, c.modify_date, c.create_user_id, c.modify_user_id, c.is_active, " +
                "co.name AS country_name, i.name AS industry_name, o.name AS org_form_name " +
                "FROM contractor c " +
                "LEFT JOIN country co ON c.country = co.id " +
                "LEFT JOIN industry i ON c.industry = i.id " +
                "LEFT JOIN org_form o ON c.org_form = o.id " +
                "WHERE c.id = ?";
        List<Contractor> contractors = jdbcTemplate.query(sql, RowMappers.CONTRACTOR_ROW_MAPPER, id);
        return contractors.stream().findFirst();
    }

    /**
     * Сохраняет нового контрагента или обновляет существующего.
     * Служебные поля (create_date, modify_date, create_user_id, modify_user_id, is_active)
     * управляются базой данных или не изменяются.
     *
     * @param contractor Объект {@link Contractor} для сохранения или обновления.
     * @return Сохраненный или обновленный объект {@link Contractor}.
     */
    public Contractor save(Contractor contractor) {
        // Проверяем, существует ли контрагент с таким ID
        String checkSql = "SELECT COUNT(*) FROM contractor WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, contractor.getId());

        if (count != null && count > 0) {
            // Обновление существующего контрагента
            String sql = "UPDATE contractor SET " +
                    "parent_id = ?, name = ?, name_full = ?, inn = ?, ogrn = ?, " +
                    "country = ?, industry = ?, org_form = ?, " +
                    "modify_date = NOW(), modify_user_id = ? " + // Обновляем служебные поля
                    "WHERE id = ?";

            jdbcTemplate.update(sql,
                    contractor.getParentId(), contractor.getName(), contractor.getNameFull(),
                    contractor.getInn(), contractor.getOgrn(),
                    contractor.getCountryId(), contractor.getIndustryId(), contractor.getOrgFormId(),
                    "sergej",
                    contractor.getId());
        } else {
            // Вставка нового контрагента
            String sql = "INSERT INTO contractor (" +
                    "id, parent_id, name, name_full, inn, ogrn, " +
                    "country, industry, org_form, " +
                    "create_date, modify_date, create_user_id, modify_user_id, is_active" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?, TRUE)"; // DEFAULT TRUE для is_active

            // ставим заглушку 'sergej'
            jdbcTemplate.update(sql,
                    contractor.getId(), contractor.getParentId(), contractor.getName(),
                    contractor.getNameFull(), contractor.getInn(), contractor.getOgrn(),
                    contractor.getCountryId(), contractor.getIndustryId(), contractor.getOrgFormId(),
                    "sergej", "sergej"
            );
        }
        // Возвращаем контрагента, возможно, с обновленными служебными полями, если они были считаны
        // Для этого можно было бы сделать findById после save/update, но это добавляет еще один запрос.
        // Для простоты, возвращаем исходный объект.
        return contractor;
    }

    public Contractor save(Contractor contractor, Long userId) {
        // Проверяем, существует ли контрагент с таким ID
        String checkSql = "SELECT COUNT(*) FROM contractor WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, contractor.getId());

        if (count != null && count > 0) {
            // Обновление существующего контрагента
            String sql = "UPDATE contractor SET " +
                    "parent_id = ?, name = ?, name_full = ?, inn = ?, ogrn = ?, " +
                    "country = ?, industry = ?, org_form = ?, " +
                    "modify_date = NOW(), modify_user_id = ? " + // Обновляем служебные поля
                    "WHERE id = ?";

            jdbcTemplate.update(sql,
                    contractor.getParentId(), contractor.getName(), contractor.getNameFull(),
                    contractor.getInn(), contractor.getOgrn(),
                    contractor.getCountryId(), contractor.getIndustryId(), contractor.getOrgFormId(),
                    userId,
                    contractor.getId());
        } else {
            // Вставка нового контрагента
            String sql = "INSERT INTO contractor (" +
                    "id, parent_id, name, name_full, inn, ogrn, " +
                    "country, industry, org_form, " +
                    "create_date, modify_date, create_user_id, modify_user_id, is_active" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?, TRUE)"; // DEFAULT TRUE для is_active

            jdbcTemplate.update(sql,
                    contractor.getId(), contractor.getParentId(), contractor.getName(),
                    contractor.getNameFull(), contractor.getInn(), contractor.getOgrn(),
                    contractor.getCountryId(), contractor.getIndustryId(), contractor.getOrgFormId(),
                    userId, userId
            );
        }

        return contractor;
    }

    /**
     * Выполняет логическое удаление контрагента, устанавливая поле `is_active` в `FALSE`.
     *
     * @param id ID контрагента, который нужно удалить.
     * @return Количество затронутых строк (1, если успешно, 0, если контрагент не найден).
     */
    public int delete(String id) {
        String sql = "UPDATE contractor SET is_active = FALSE, modify_date = NOW(), modify_user_id = ? WHERE id = ?";

        return jdbcTemplate.update(sql, "sergej", id);
    }

    /**
     * Выполняет поиск контрагента с возможностью фильтрации и пагинации.
     *
     * @param filters Карта (Map) фильтров, где ключ - имя поля, значение - критерий поиска.
     * @param page Номер страницы (начиная с 0).
     * @param size Количество элементов на странице.
     * @return Список объектов {@link Contractor}, соответствующих критериям.
     */
    public List<Contractor> search(Map<String, String> filters, int page, int size) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT c.id, c.parent_id, c.name, c.name_full, c.inn, c.ogrn, ")
                .append("c.country, c.industry, c.org_form, ")
                .append("c.create_date, c.modify_date, c.create_user_id, c.modify_user_id, c.is_active, ")
                .append("co.name AS country_name, i.name AS industry_name, o.name AS org_form_name ")
                .append("FROM contractor c ")
                .append("LEFT JOIN country co ON c.country = co.id ")
                .append("LEFT JOIN industry i ON c.industry = i.id ")
                .append("LEFT JOIN org_form o ON c.org_form = o.id ")
                .append("WHERE c.is_active = TRUE");

        List<Object> params = new ArrayList<>();

        filters.forEach((key, value) -> {
            switch (key) {

                case "contractor_id" -> {
                    // Точное совпадение
                    sqlBuilder.append(" AND c.id = ?");
                    params.add(value);
                }

                case "parent_id" -> {
                    // Точное совпадение
                    sqlBuilder.append(" AND c.parent_id = ?");
                    params.add(value);
                }

                case "contractor_search" -> {
                    // Частичное совпадение по нескольким полям
                    sqlBuilder.append(" AND (LOWER(c.name) LIKE LOWER(?)" +
                            " OR LOWER(c.name_full) LIKE LOWER(?)" +
                            " OR LOWER(c.inn) LIKE LOWER(?)" +
                            " OR LOWER(c.ogrn) LIKE LOWER(?))");
                    String likeValue = "%" + value + "%";
                    params.add(likeValue);
                    params.add(likeValue);
                    params.add(likeValue);
                    params.add(likeValue);
                }

                case "country" -> {
                    // Частичное совпадение по country.name
                    sqlBuilder.append(" AND LOWER(co.name) LIKE LOWER(?)");
                    params.add("%" + value + "%");
                }

                case "countryId" -> {
                    sqlBuilder.append(" AND co.id = ?");
                    params.add(value);
                }

                case "industry" -> {
                    // Точное совпадение
                    sqlBuilder.append(" AND c.industry = ?");
                    try {
                        params.add(Integer.parseInt(value));
                    } catch (NumberFormatException e) {

                        LOGGER.warn("Неверный формат industry ID: " + value);

                    }
                }

                case "org_form" -> {
                    // Частичное совпадение по org_form.name
                    sqlBuilder.append(" AND LOWER(o.name) LIKE LOWER(?)");
                    params.add("%" + value + "%");
                }
                default -> {
                    LOGGER.warn("Необрабатываемый фильтр: {}", key);
                }
            }
        });

        // Добавляем пагинацию
        sqlBuilder.append(" ORDER BY c.id"); // Важно для консистентной пагинации
        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size); // Вычисляем OFFSET

        return jdbcTemplate.query(sqlBuilder.toString(), RowMappers.CONTRACTOR_ROW_MAPPER, params.toArray());
    }

    /**
     * Получает общее количество активных контрагенты, соответствующих заданным фильтрам.
     * Используется для расчета общего количества страниц в пагинации.
     *
     * @param filters Карта (Map) фильтров, где ключ - имя поля, значение - критерий поиска.
     * @return Общее количество активных контрагентов, соответствующих фильтрам.
     */
    public int countSearch(Map<String, String> filters) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) ")
                .append("FROM contractor c ")
                .append("LEFT JOIN country co ON c.country = co.id ")
                .append("LEFT JOIN industry i ON c.industry = i.id ")
                .append("LEFT JOIN org_form o ON c.org_form = o.id ")
                .append("WHERE c.is_active = TRUE"); // Только активные контрагенты

        List<Object> params = new ArrayList<>();

        filters.forEach((key, value) -> {
            switch (key) {
                case "contractor_id" -> {
                    sqlBuilder.append(" AND c.id = ?");
                    params.add(value);
                }

                case "parent_id" -> {
                    sqlBuilder.append(" AND c.parent_id = ?");
                    params.add(value);
                }
                case "contractor_search" -> {
                    sqlBuilder.append(" AND (LOWER(c.name) LIKE LOWER(?)" +
                            " OR LOWER(c.name_full) LIKE LOWER(?)" +
                            " OR LOWER(c.inn) LIKE LOWER(?)" +
                            " OR LOWER(c.ogrn) LIKE LOWER(?))");
                    String likeValue = "%" + value + "%";
                    params.add(likeValue);
                    params.add(likeValue);
                    params.add(likeValue);
                    params.add(likeValue);
                }
                case "country" -> {
                    sqlBuilder.append(" AND LOWER(co.name) LIKE LOWER(?)");
                    params.add("%" + value + "%");
                }
                case "industry" -> {
                    sqlBuilder.append(" AND c.industry = ?");

                    try {
                        params.add(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        LOGGER.warn("Неправильный industry ID формат для фильтра '" + key + "': '" + value + "'. Пропуск фильтра.", e);
                    }
                }
                case "org_form" -> {
                    sqlBuilder.append(" AND LOWER(o.name) LIKE LOWER(?)");
                    params.add("%" + value + "%");
                }

                default -> {
                    LOGGER.warn("Неизвестный ключ фильтра: " + key);
                }
            }
        });

        Integer count = jdbcTemplate.queryForObject(sqlBuilder.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

}
