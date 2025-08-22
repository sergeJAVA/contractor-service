package com.example.contractor_service.service;

import com.example.contractor_service.model.Country;
import com.example.contractor_service.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private static final String COUNTRIES_PREFIX = "countries";

    @Override
    @Cacheable(value = COUNTRIES_PREFIX, key = "'all'")
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> findById(String id) {
        return countryRepository.findById(id);
    }

    @Override
    @CacheEvict(value = COUNTRIES_PREFIX, allEntries = true)
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    @CacheEvict(value = COUNTRIES_PREFIX, allEntries = true)
    public int delete(String id) {
        return countryRepository.delete(id);
    }

}
