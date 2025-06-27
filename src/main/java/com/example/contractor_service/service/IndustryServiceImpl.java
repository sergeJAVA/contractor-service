package com.example.contractor_service.service;

import com.example.contractor_service.model.Industry;
import com.example.contractor_service.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository industryRepository;

    @Override
    public List<Industry> findAll() {
        return industryRepository.findAll();
    }

    @Override
    public Optional<Industry> findById(int id) {
        return industryRepository.findById(id);
    }

    @Override
    public Industry save(Industry industry) {
        return industryRepository.save(industry);
    }

    @Override
    public int delete(int id) {
        return industryRepository.delete(id);
    }

}
