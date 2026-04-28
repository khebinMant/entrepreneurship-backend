package com.project.emprendia.shared.service;

import com.project.emprendia.shared.dto.CatalogueTypeRequest;
import com.project.emprendia.shared.dto.CatalogueTypeResponse;

import java.util.List;

public interface CatalogueTypeService {

    List<CatalogueTypeResponse> findAll();

    CatalogueTypeResponse findById(Long id);

    CatalogueTypeResponse create(CatalogueTypeRequest request);

    CatalogueTypeResponse update(Long id, CatalogueTypeRequest request);

    void delete(Long id);
}
