package com.example.contractor_service.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель ответа для результатов поиска подрядчиков.
 * Содержит список найденных подрядчиков и информацию о пагинации.
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchResponse {

    private List<Contractor> contractors;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public SearchResponse(List<Contractor> contractors, int page, int size, long totalElements) {
        this.contractors = contractors;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

}
