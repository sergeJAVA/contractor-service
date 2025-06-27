package com.example.contractor_service.data;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String SEPARATOR = ";";
    private final String fileName;
    private final String tableName;
    private boolean isCountryTable;
    private static final String START_PATH = "src/main/resources/db/changelog/data/";

    public DataLoader(JdbcTemplate jdbcTemplate,
                      String fileName, String tableName,
                      boolean isCountryId) {
        this.jdbcTemplate = jdbcTemplate;
        this.fileName = fileName;
        this.tableName = tableName;
        this.isCountryTable = isCountryId;
    }

    @PostConstruct
    public void loadData() {
        // Проверяем, были ли данные уже загружены
        // Это предотвратит повторную загрузку при каждом запуске приложения
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
            if (count != null && count > 0) {
                LOGGER.warn("Данные в таблице \"" + tableName + "\" уже загружены. Пропускаем загрузку данных!");
                System.out.println();
                return;
            }
        } catch (Exception e) {
            LOGGER.error("Неправильно указано название таблицы или её не существует! " + e.getMessage());
            e.printStackTrace();
            return;
        }

        LOGGER.info("Загрузка данных для таблицы \"" + tableName + "\" из CSV с помощью JdbcTemplate: " + START_PATH + fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(START_PATH + fileName, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = splitCsvLine(line, SEPARATOR);

                if (parts.length != 2) {
                    continue;
                }

                String id = parts[0].trim();
                String name = parts[1].trim();

                if (isCountryTable) {
                    // Выполнение INSERT запроса через JdbcTemplate для таблица country
                    String sql = String.format("INSERT INTO %s (id, name) VALUES (?, ?)", tableName);
                    jdbcTemplate.update(sql, id, name);
                } else {
                    // Выполнение INSERT запроса через JdbcTemplate
                    String sql = String.format("INSERT INTO %s (name) VALUES (?)", tableName);
                    jdbcTemplate.update(sql, name);
                }

            }
            LOGGER.info("Данные о стране успешно загружены.");

        } catch (IOException e) {
            LOGGER.error("Ошибка при загрузке данных о странах из CSV: " + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.err.println("Во время загрузки данных произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] splitCsvLine(String line, String separator) throws IOException {
        int separatorIndex = line.indexOf(separator);
        if (separatorIndex == -1) {
            throw new IOException("Ошибка в структуре файла! Проверьте файл!");
        }

        String id = line.substring(0, separatorIndex);
        String name = line.substring(separatorIndex + separator.length());

        if (name.equals("-")) {
            return new String[]{};
        }
        return new String[]{id, name};
    }

}
