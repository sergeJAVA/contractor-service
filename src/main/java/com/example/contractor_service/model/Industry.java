package com.example.contractor_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель данных, представляющая сущность "Отрасль".
 * Является частью справочной информации.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Industry {

    private int id;
    private String name;

}
