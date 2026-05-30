package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationResponse;

import java.util.List;

public interface EntrepreneurshipLocationService {
    List<EntrepreneurshipLocationResponse> findByEntrepreneurshipId(Long entrepreneurshipId);
    EntrepreneurshipLocationResponse findById(Long id);
    EntrepreneurshipLocationResponse create(EntrepreneurshipLocationRequest request);
    EntrepreneurshipLocationResponse update(Long id, EntrepreneurshipLocationRequest request);
    void delete(Long id);
}

