package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;

import java.util.List;

public interface EntrepreneurshipService {

    List<EntrepreneurshipResponse> findAll();

    EntrepreneurshipResponse findById(Long id);

    List<EntrepreneurshipResponse> findByUserId(Long userId);

    List<EntrepreneurshipResponse> search(String name, Long categoryId, Boolean isPhysical, Boolean isDigital);

    EntrepreneurshipResponse create(EntrepreneurshipRequest request);

    EntrepreneurshipResponse update(Long id, EntrepreneurshipRequest request);

    void delete(Long id);
}
