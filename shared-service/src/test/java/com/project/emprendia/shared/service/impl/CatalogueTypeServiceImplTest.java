package com.project.emprendia.shared.service.impl;

import com.project.emprendia.shared.domain.CatalogueType;
import com.project.emprendia.shared.dto.CatalogueTypeRequest;
import com.project.emprendia.shared.dto.CatalogueTypeResponse;
import com.project.emprendia.shared.exception.DuplicateResourceException;
import com.project.emprendia.shared.exception.ResourceNotFoundException;
import com.project.emprendia.shared.mapping.mapper.CatalogueTypeMapper;
import com.project.emprendia.shared.repository.CatalogueTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogueTypeServiceImplTest {

    @Mock
    private CatalogueTypeRepository catalogueTypeRepository;

    @Mock
    private CatalogueTypeMapper catalogueTypeMapper;

    @InjectMocks
    private CatalogueTypeServiceImpl catalogueTypeService;

    @Test
    void findAll_shouldReturnMappedList() {
        CatalogueType entity = new CatalogueType();
        CatalogueTypeResponse response = CatalogueTypeResponse.builder().code("COUNTRY").build();
        when(catalogueTypeRepository.findAll()).thenReturn(List.of(entity));
        when(catalogueTypeMapper.toResponse(entity)).thenReturn(response);

        List<CatalogueTypeResponse> result = catalogueTypeService.findAll();

        assertThat(result).hasSize(1).contains(response);
    }

    @Test
    void findById_withValidId_shouldReturnResponse() {
        CatalogueType entity = new CatalogueType();
        CatalogueTypeResponse response = CatalogueTypeResponse.builder().catalogueTypeId(1L).build();
        when(catalogueTypeRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(catalogueTypeMapper.toResponse(entity)).thenReturn(response);

        CatalogueTypeResponse result = catalogueTypeService.findById(1L);

        assertThat(result.getCatalogueTypeId()).isEqualTo(1L);
    }

    @Test
    void findById_withInvalidId_shouldThrowException() {
        when(catalogueTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogueTypeService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withDuplicateCode_shouldThrowException() {
        CatalogueTypeRequest request = new CatalogueTypeRequest();
        request.setCode("COUNTRY");
        when(catalogueTypeRepository.existsByCode("COUNTRY")).thenReturn(true);

        assertThatThrownBy(() -> catalogueTypeService.create(request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_withValidRequest_shouldReturnSavedEntity() {
        CatalogueTypeRequest request = new CatalogueTypeRequest();
        request.setCode("CITY");
        CatalogueType entity = new CatalogueType();
        CatalogueTypeResponse response = CatalogueTypeResponse.builder().code("CITY").build();

        when(catalogueTypeRepository.existsByCode("CITY")).thenReturn(false);
        when(catalogueTypeMapper.toEntity(request)).thenReturn(entity);
        when(catalogueTypeRepository.save(entity)).thenReturn(entity);
        when(catalogueTypeMapper.toResponse(entity)).thenReturn(response);

        CatalogueTypeResponse result = catalogueTypeService.create(request);

        assertThat(result.getCode()).isEqualTo("CITY");
    }
}
