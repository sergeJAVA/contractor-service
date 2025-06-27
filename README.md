Задание №1
# Сервис для управления контрагентами

Этот проект представляет собой RESTful сервис для управления контрагентами и связанными справочниками (страны, отрасли, организационно-правовые формы). Разработан на Spring Boot с использованием `JdbcTemplate` для взаимодействия с PostgreSQL. Схема базы данных и начальные данные инициализируются с помощью Liquibase и пользовательского загрузчика данных.

## Требования

-   **Java 21+**
-   **Maven**
-   **Docker** (для PostgreSQL)

## Перед запуском приложения нужно сначала поднять образ postgres, который находиться в docker-compose.yml

  **Запустить PostgreSQL**:
    В корневой директории проекта выполните:
    ```bash
    docker-compose up 
    ```

## API Эндпоинты

**Базовый URL**: `http://localhost:8080`

### Контрагенты (`/contractor`)

-   `GET /{id}`: Получить контрагента по ID.
-   `PUT /save`: Сохранить/обновить контрагента.
-   `DELETE /delete/{id}`: Логическое удаление контрагента (`is_active = FALSE`).
-   `POST /search`: Поиск активных контрагентов с фильтрацией и пагинацией.

### Справочники (`/country`, `/industry`, `/org_form`)

-   `GET /all`: Получить все записи.
-   `GET /{id}`: Получить запись по ID.
-   `PUT /save`: Сохранить/обновить запись.
-   `DELETE /delete/{id}`:
    -   `country`: Логическое удаление (`is_active = FALSE`).
    -   `industry`, `org_form`: Физическое удаление.

## Тестирование API

Для тестирования используйте любой HTTP-клиент (например, Postman, cURL).

**Шаги для тестирования:**

1.  **Создать первого контрагента**:
    `PUT http://localhost:8080/contractor/save`
    **Body (JSON)**:
    ```json
    {
        "id": "CONTRACT",
        "parentId": null,
        "name": "Тестовый Контрагент",
        "nameFull": "Полное Наименование Тестового Контрагента",
        "inn": "1234567890",
        "ogrn": "0987654321",
        "countryId": "RUS",
        "industryId": 1,
        "orgFormId": 1
    }
    ```

2.  **Найти первого контрагента по ID**:
    `GET http://localhost:8080/contractor/CONTRACT`

3.  **Изменить первого контрагента (обновить `countryId`, `industryId`, `orgFormId`)**:
    `PUT http://localhost:8080/contractor/save`
    **Body (JSON)**:
    ```json
    {
        "id": "CONTRACT",
        "parentId": null,
        "name": "Тестовый Контрагент",
        "nameFull": "Полное Наименование Тестового Контрагента",
        "inn": "1234567890",
        "ogrn": "0987654321",
        "countryId": "ABH",
        "industryId": 25,
        "orgFormId": 14
    }
    ```

4.  **Проверить изменения первого контрагента**:
    `GET http://localhost:8080/contractor/CONTRACT`

5.  **Создать второго контрагента (с родителем)**:
    `PUT http://localhost:8080/contractor/save`
    **Body (JSON)**:
    ```json
    {
        "id": "CONTRACT2",
        "parentId": "CONTRACT",
        "name": "Тестовый Второй Контрагент",
        "nameFull": "Полное Наименование Тестового Второго Контрагента",
        "inn": "1234567890",
        "ogrn": "0987654321",
        "countryId": "ABH",
        "industryId": 66,
        "orgFormId": 33
    }
    ```

6.  **Выполнить поиск контрагентов (например, по родителю и орг. форме)**:
    `POST http://localhost:8080/contractor/search`
    **Body (JSON)**:
    ```json
    {
      "filters": {
        // "contractor_search": "09",
        // "contractor_id": "CONTRACT2",
        // "industry": 66,
        "parent_id": "CONTRACT",
        "org_form": "Казачье"
      },
      "page": 0,
      "size": 5
    }
    ```
    *Примечание*: Вы можете раскомментировать другие фильтры (`contractor_search`, `contractor_id`, `industry`) для проверки различных сценариев поиска.

7. ## Чтобы запустить интеграционные тесты, сначала нужно запустить файл docker-compose-test.yml для поднятия тестового окружения.
