package com.example.contractor_service.config;

import com.example.contractor_service.util.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Конфигурационный класс для инициализации и загрузки данных в базу данных
 * при старте Spring-приложения.
 * Определяет {@link DataLoader} бины для различных таблиц, указывая
 * соответствующий CSV-файл и имя таблицы.
 *
 * @author sergeJAVA
 */
@Configuration
public class DataInitConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Создает бин {@link DataLoader} для загрузки данных в таблицу "country" из файла "country.csv".
     * Для таблицы "country" используется специальная логика парсинга, так как
     * поле (ID) не является SERIAL.
     *
     * @return Экземпляр {@link DataLoader} для таблицы "country".
     */
    @Bean
    public DataLoader countryTable() {
        return new DataLoader(jdbcTemplate, "country.csv", "country", true);
    }

    /**
     * Создает бин {@link DataLoader} для загрузки данных в таблицу "org_form" из файла "org_form.csv".
     * Для этой таблицы предполагается, что первое поле (ID) не используется для вставки,
     * а вставляется только второе поле (name), так как isCountryId установлено в false.
     *
     * @return Экземпляр {@link DataLoader} для таблицы "org_form".
     */
    @Bean
    public DataLoader industryTable() {
        return new DataLoader(jdbcTemplate, "industry.csv", "industry", false);
    }

    /**
     * Создает бин {@link DataLoader} для загрузки данных в таблицу "org_form" из файла "org_form.csv".
     * Для этой таблицы предполагается, что первое поле (ID) не используется для вставки,
     * а вставляется только второе поле (name), так как isCountryId установлено в false.
     *
     * @return Экземпляр {@link DataLoader} для таблицы "org_form".
     */
    @Bean
    public DataLoader orgformTable() {
        return new DataLoader(jdbcTemplate, "org_form.csv", "org_form", false);
    }

}
