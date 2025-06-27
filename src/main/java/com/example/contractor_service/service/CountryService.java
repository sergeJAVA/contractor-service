package com.example.contractor_service.service;

import com.example.contractor_service.model.Country;

import java.util.List;
import java.util.Optional;

public interface CountryService {

    List<Country> findAll();

    Optional<Country> findById(String id);

    Country save(Country country);

    int delete(String id);

}
