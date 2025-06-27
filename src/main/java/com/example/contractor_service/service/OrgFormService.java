package com.example.contractor_service.service;

import com.example.contractor_service.model.OrgForm;

import java.util.List;
import java.util.Optional;

public interface OrgFormService {

    List<OrgForm> findAll();

    Optional<OrgForm> findById(int id);

    OrgForm save(OrgForm orgForm);

    int delete(int id);

}
