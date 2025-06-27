package com.example.contractor_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель данных, представляющая сущность "Страна".
 * Является частью справочной информации.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Country {

    private String id;
    private String name;

}
