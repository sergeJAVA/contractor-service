package com.example.contractor_service.data;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Утилитарный класс для загрузки данных из CSV-файлов в базу данных с использованием {@link JdbcTemplate}.
 * Класс предназначен для однократной загрузки данных при старте приложения,
 * проверяя наличие данных в таблице перед началом загрузки.
 * Поддерживает специфический формат CSV, где поле ID отделено от поля NAME
 * точкой с запятой. Также учитывает, является ли таблица таблицей country или
 * другой таблицей, куда вставляется только NAME, а ID генерируется с помощью автоинкрементации.
 *
 * @author sergeJAVA
 */
public class DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final JdbcTemplate jdbcTemplate;
    private static final String SEPARATOR = ";";
    private final String fileName;
    private final String tableName;
    private boolean isCountryTable;
    private static final String START_PATH = "src/main/resources/db/changelog/data/";

    /**
     * Конструктор для создания экземпляра {@link DataLoader}.
     *
     * @param jdbcTemplate   Экземпляр {@link JdbcTemplate} для взаимодействия с базой данных.
     * @param fileName       Имя CSV-файла, из которого будут загружаться данные.
     * @param tableName      Имя таблицы в базе данных, куда будут загружаться данные.
     * <p>isCountryTable Флаг, указывающий, является ли целевая таблица таблицей стран.
     *  Если true, вставляются ID и NAME. Если false, вставляется только NAME.</p>
     */
    public DataLoader(JdbcTemplate jdbcTemplate,
                      String fileName, String tableName,
                      boolean isCountryId) {
        this.jdbcTemplate = jdbcTemplate;
        this.fileName = fileName;
        this.tableName = tableName;
        this.isCountryTable = isCountryId;
    }

    /**
     * Метод, выполняемый после инициализации бина Spring.
     * Отвечает за загрузку данных из CSV-файла в соответствующую таблицу базы данных.
     * Проверяет, существуют ли уже данные в таблице, чтобы избежать повторной загрузки.
     * Обрабатывает специфический формат CSV, где первое поле до точки с запятой - ID,
     * а все остальное - NAME.
     *
     * @throws IOException Если возникает ошибка при чтении CSV-файла.
     */
    @PostConstruct
    public void loadData() {
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
                    // Выполнение INSERT запроса через JdbcTemplate для таблицы country
                    String sql = String.format("INSERT INTO %s (id, name) VALUES (?, ?)", tableName);
                    jdbcTemplate.update(sql, id, name);
                } else {
                    // Выполнение INSERT запроса через JdbcTemplate для других таблиц
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

    /**
     * Вспомогательный метод для парсинга одной строки CSV.
     * Предназначен для CSV-файлов с двумя полями, разделенными заданным {@code separator}.
     * Корректно обрабатывает случай, когда второе поле (NAME) содержит символ-разделитель (запятую),
     * так как оно не обрамляется кавычками.
     * Если второе поле равно "-", возвращает пустой массив строк, что будет проигнорировано вызывающим методом.
     *
     * @param line      Строка из CSV-файла для парсинга.
     * @param separator Символ-разделитель полей в строке (например, ";").
     * @return Массив строк, содержащий ID и NAME, или пустой массив, если NAME равно "-".
     * @throws IOException Если в строке не найден разделитель, что указывает на некорректную структуру файла.
     */
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
