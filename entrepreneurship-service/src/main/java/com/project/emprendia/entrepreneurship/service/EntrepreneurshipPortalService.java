package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipPortalRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipPortalResponse;

public interface EntrepreneurshipPortalService {
    EntrepreneurshipPortalResponse findByEntrepreneurshipId(Long entrepreneurshipId);
    EntrepreneurshipPortalResponse findById(Long id);
    EntrepreneurshipPortalResponse create(EntrepreneurshipPortalRequest request);
    EntrepreneurshipPortalResponse update(Long id, EntrepreneurshipPortalRequest request);
    void delete(Long id);
}

