package com.example.contractor_service.service;

import com.example.contractor_service.model.OrgForm;
import com.example.contractor_service.repository.OrgFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrgFormServiceImpl implements OrgFormService {

    private final OrgFormRepository orgFormRepository;
    private static final String ORG_FORMS_PREFIX = "orgforms";

    @Override
    @Cacheable(value = ORG_FORMS_PREFIX, key = "'all'")
    public List<OrgForm> findAll() {
        return orgFormRepository.findAll();
    }

    @Override
    public Optional<OrgForm> findById(int id) {
        return orgFormRepository.findById(id);
    }

    @Override
    @CacheEvict(value = ORG_FORMS_PREFIX, allEntries = true)
    public OrgForm save(OrgForm orgForm) {
        return orgFormRepository.save(orgForm);
    }

    @Override
    @CacheEvict(value = ORG_FORMS_PREFIX, allEntries = true)
    public int delete(int id) {
        return orgFormRepository.delete(id);
    }

}
