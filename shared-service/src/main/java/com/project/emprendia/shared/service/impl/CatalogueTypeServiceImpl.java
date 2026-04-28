package com.project.emprendia.shared.service.impl;

import com.project.emprendia.shared.domain.CatalogueType;
import com.project.emprendia.shared.dto.CatalogueTypeRequest;
import com.project.emprendia.shared.dto.CatalogueTypeResponse;
import com.project.emprendia.shared.exception.DuplicateResourceException;
import com.project.emprendia.shared.exception.ResourceNotFoundException;
import com.project.emprendia.shared.mapping.mapper.CatalogueTypeMapper;
import com.project.emprendia.shared.repository.CatalogueTypeRepository;
import com.project.emprendia.shared.service.CatalogueTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogueTypeServiceImpl implements CatalogueTypeService {

    private final CatalogueTypeRepository catalogueTypeRepository;
    private final CatalogueTypeMapper catalogueTypeMapper;

    @Override
    public List<CatalogueTypeResponse> findAll() {
        return catalogueTypeRepository.findAll().stream()
            .map(catalogueTypeMapper::toResponse)
            .toList();
    }

    @Override
    public CatalogueTypeResponse findById(Long id) {
        CatalogueType entity = catalogueTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CatalogueType", id));
        return catalogueTypeMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public CatalogueTypeResponse create(CatalogueTypeRequest request) {
        if (catalogueTypeRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("CatalogueType", "code", request.getCode());
        }
        CatalogueType entity = catalogueTypeMapper.toEntity(request);
        return catalogueTypeMapper.toResponse(catalogueTypeRepository.save(entity));
    }

    @Override
    @Transactional
    public CatalogueTypeResponse update(Long id, CatalogueTypeRequest request) {
        CatalogueType entity = catalogueTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CatalogueType", id));
        catalogueTypeMapper.updateEntityFromRequest(request, entity);
        return catalogueTypeMapper.toResponse(catalogueTypeRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!catalogueTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("CatalogueType", id);
        }
        catalogueTypeRepository.deleteById(id);
    }
}
