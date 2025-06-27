package com.example.contractor_service.service;

import com.example.contractor_service.model.Contractor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ContractorService {

    Optional<Contractor> findById(String id);

    Contractor save(Contractor contractor);

    int delete(String id);

    List<Contractor> search(Map<String, String> filters, int page, int size);

    int countSearch(java.util.Map<String, String> filters);

}
