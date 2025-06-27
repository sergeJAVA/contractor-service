package com.example.contractor_service.service;

import com.example.contractor_service.model.Industry;

import java.util.List;
import java.util.Optional;

public interface IndustryService {

    List<Industry> findAll();

    Optional<Industry> findById(int id);

    Industry save(Industry industry);

    int delete(int id);

}
