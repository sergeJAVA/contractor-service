package com.example.contractor_service.util;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.Country;
import com.example.contractor_service.model.Industry;
import com.example.contractor_service.model.OrgForm;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Вспомогательный класс, содержащий {@link RowMapper} для преобразования
 * результатов запросов {@link ResultSet} в соответствующие POJO-объекты.
 *
 * @author sergeJAVA
 */
public final class RowMappers {

    private RowMappers() {

    }

    /**
     * {@link RowMapper} для преобразования строки {@link ResultSet} в объект {@link Country}.
     */
    public static final RowMapper<Country> COUNTRY_ROW_MAPPER = (rs, rowNum) -> {
        Country country = new Country();
        country.setId(rs.getString("id"));
        country.setName(rs.getString("name"));
        return country;
    };

    /**
     * {@link RowMapper} для преобразования строки {@link ResultSet} в объект {@link Industry}.
     */
    public static final RowMapper<Industry> INDUSTRY_ROW_MAPPER = (rs, rowNum) -> {
        Industry industry = new Industry();
        industry.setId(rs.getInt("id"));
        industry.setName(rs.getString("name"));
        return industry;
    };

    /**
     * {@link RowMapper} для преобразования строки {@link ResultSet} в объект {@link OrgForm}.
     */
    public static final RowMapper<OrgForm> ORG_FORM_ROW_MAPPER = (rs, rowNum) -> {
        OrgForm orgForm = new OrgForm();
        orgForm.setId(rs.getInt("id"));
        orgForm.setName(rs.getString("name"));
        return orgForm;
    };

    /**
     * {@link RowMapper} для преобразования строки {@link ResultSet} в объект {@link Contractor}.
     * Этот маппер предназначен для запросов, которые объединяют данные из таблиц `country`, `industry`, `org_form`.
     * Он заполняет как основные поля контрагента, так и связанные поля (названия страны, индустрии, орг. формы).
     */
    public static final RowMapper<Contractor> CONTRACTOR_ROW_MAPPER = (rs, rowNum) -> {
        Contractor contractor = new Contractor();
        contractor.setId(rs.getString("id"));
        contractor.setParentId(rs.getString("parent_id"));
        contractor.setName(rs.getString("name"));
        contractor.setNameFull(rs.getString("name_full"));
        contractor.setInn(rs.getString("inn"));
        contractor.setOgrn(rs.getString("ogrn"));
        contractor.setCountryId(rs.getString("country")); // В БД это FK, в POJO его тоже сохраняем
        contractor.setIndustryId(rs.getObject("industry", Integer.class));
        contractor.setOrgFormId(rs.getObject("org_form", Integer.class));

        // Служебные поля
        Timestamp createTimestamp = rs.getTimestamp("create_date");
        if (createTimestamp != null) {
            contractor.setCreateDate(createTimestamp.toLocalDateTime());
        }
        Timestamp modifyTimestamp = rs.getTimestamp("modify_date");
        if (modifyTimestamp != null) {
            contractor.setModifyDate(modifyTimestamp.toLocalDateTime());
        }
        contractor.setCreateUserId(rs.getString("create_user_id"));
        contractor.setModifyUserId(rs.getString("modify_user_id"));
        contractor.setIsActive(rs.getBoolean("is_active"));

        // Связанные поля (из JOIN'ов)
        contractor.setCountryName(rs.getString("country_name"));
        contractor.setIndustryName(rs.getString("industry_name"));
        contractor.setOrgFormName(rs.getString("org_form_name"));

        return contractor;
    };

}
