package com.example.contractor_service.service;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.repository.ContractorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepository contractorRepository;

    @Override
    public Optional<Contractor> findById(String id) {
        return contractorRepository.findById(id);
    }

    @Override
    public Contractor save(Contractor contractor) {
        return contractorRepository.save(contractor);
    }

    @Override
    public int delete(String id) {
        return contractorRepository.delete(id);
    }

    @Override
    public List<Contractor> search(Map<String, String> filters, int page, int size) {
        return contractorRepository.search(filters, page, size);
    }

    @Override
    public int countSearch(Map<String, String> filters) {
        return contractorRepository.countSearch(filters);
    }

}
