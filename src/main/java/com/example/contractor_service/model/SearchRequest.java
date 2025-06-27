package com.example.contractor_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Модель запроса для поиска подрядчиков с применением фильтров и пагинации.
 * Используется в POST /contractor/search.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SearchRequest {

    private Map<String, String> filters;
    private int page;
    private int size;

}
