package com.example.contractor_service.service;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.repository.ContractorRepository;
import com.webbee.audit_lib.annotation.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepository contractorRepository;
    private static final String CONTRACTORS_PREFIX = "contractors";

    @Override
    @Cacheable(value = CONTRACTORS_PREFIX, key = "'all'")
    public List<Contractor> findAll() {
        return contractorRepository.findAll();
    }

    @Override
    public Optional<Contractor> findById(String id) {
        return contractorRepository.findById(id);
    }

    @Override
    @AuditLog
    @CacheEvict(value = CONTRACTORS_PREFIX, allEntries = true)
    public Contractor save(Contractor contractor) {
        return contractorRepository.save(contractor);
    }

    @Override
    @AuditLog
    @CacheEvict(value = CONTRACTORS_PREFIX, allEntries = true)
    public Contractor save(Contractor contractor, Long userId) {
        return contractorRepository.save(contractor, userId);
    }

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    @CacheEvict(value = CONTRACTORS_PREFIX, allEntries = true)
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
