package com.project.emprendia.shared.service;

import com.project.emprendia.shared.dto.CatalogueValueRequest;
import com.project.emprendia.shared.dto.CatalogueValueResponse;

import java.util.List;

public interface CatalogueValueService {

    List<CatalogueValueResponse> findByTypeCode(String typeCode);

    CatalogueValueResponse findById(Long id);

    List<CatalogueValueResponse> findChildrenByParentId(Long parentId);

    CatalogueValueResponse create(CatalogueValueRequest request);

    CatalogueValueResponse update(Long id, CatalogueValueRequest request);

    void delete(Long id);
}
