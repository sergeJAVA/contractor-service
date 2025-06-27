package com.example.contractor_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель данных, представляющая сущность "Организационно-правовая форма".
 * Является частью справочной информации.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrgForm {

    private int id;
    private String name;

}
