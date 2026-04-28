package com.project.emprendia.shared.service.impl;

import com.project.emprendia.shared.domain.CatalogueType;
import com.project.emprendia.shared.domain.CatalogueValue;
import com.project.emprendia.shared.dto.CatalogueValueRequest;
import com.project.emprendia.shared.dto.CatalogueValueResponse;
import com.project.emprendia.shared.exception.ResourceNotFoundException;
import com.project.emprendia.shared.mapping.mapper.CatalogueValueMapper;
import com.project.emprendia.shared.repository.CatalogueTypeRepository;
import com.project.emprendia.shared.repository.CatalogueValueQueryRepository;
import com.project.emprendia.shared.repository.CatalogueValueRepository;
import com.project.emprendia.shared.service.CatalogueValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogueValueServiceImpl implements CatalogueValueService {

    private final CatalogueValueRepository catalogueValueRepository;
    private final CatalogueValueQueryRepository catalogueValueQueryRepository;
    private final CatalogueTypeRepository catalogueTypeRepository;
    private final CatalogueValueMapper catalogueValueMapper;

    @Override
    public List<CatalogueValueResponse> findByTypeCode(String typeCode) {
        return catalogueValueQueryRepository.findByTypeCode(typeCode).stream()
            .map(catalogueValueMapper::toResponse)
            .toList();
    }

    @Override
    public CatalogueValueResponse findById(Long id) {
        CatalogueValue entity = catalogueValueRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CatalogueValue", id));
        return catalogueValueMapper.toResponse(entity);
    }

    @Override
    public List<CatalogueValueResponse> findChildrenByParentId(Long parentId) {
        return catalogueValueQueryRepository.findChildrenByParentId(parentId).stream()
            .map(catalogueValueMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public CatalogueValueResponse create(CatalogueValueRequest request) {
        CatalogueType catalogueType = catalogueTypeRepository.findById(request.getCatalogueTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("CatalogueType", request.getCatalogueTypeId()));

        CatalogueValue entity = catalogueValueMapper.toEntity(request);
        entity.setCatalogueType(catalogueType);

        if (request.getParentValueId() != null) {
            CatalogueValue parent = catalogueValueRepository.findById(request.getParentValueId())
                .orElseThrow(() -> new ResourceNotFoundException("CatalogueValue (parent)", request.getParentValueId()));
            entity.setParentValue(parent);
        }

        return catalogueValueMapper.toResponse(catalogueValueRepository.save(entity));
    }

    @Override
    @Transactional
    public CatalogueValueResponse update(Long id, CatalogueValueRequest request) {
        CatalogueValue entity = catalogueValueRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("CatalogueValue", id));
        catalogueValueMapper.updateEntityFromRequest(request, entity);
        return catalogueValueMapper.toResponse(catalogueValueRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!catalogueValueRepository.existsById(id)) {
            throw new ResourceNotFoundException("CatalogueValue", id);
        }
        catalogueValueRepository.deleteById(id);
    }
}
