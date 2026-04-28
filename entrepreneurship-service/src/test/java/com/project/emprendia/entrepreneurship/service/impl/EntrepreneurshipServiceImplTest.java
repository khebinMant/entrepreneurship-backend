package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.domain.Category;
import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.mapping.mapper.EntrepreneurshipMapper;
import com.project.emprendia.entrepreneurship.repository.CategoryRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipQueryRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipRepository;
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
class EntrepreneurshipServiceImplTest {

    @Mock private EntrepreneurshipRepository entrepreneurshipRepository;
    @Mock private EntrepreneurshipQueryRepository entrepreneurshipQueryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private EntrepreneurshipMapper entrepreneurshipMapper;

    @InjectMocks
    private EntrepreneurshipServiceImpl entrepreneurshipService;

    @Test
    void findById_withInvalidId_shouldThrow() {
        when(entrepreneurshipRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> entrepreneurshipService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withInvalidCategory_shouldThrow() {
        EntrepreneurshipRequest request = new EntrepreneurshipRequest();
        request.setCategoryId(99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> entrepreneurshipService.create(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnMappedList() {
        Entrepreneurship entity = new Entrepreneurship();
        EntrepreneurshipResponse response = EntrepreneurshipResponse.builder().entrepreneurshipId(1L).build();
        when(entrepreneurshipRepository.findAll()).thenReturn(List.of(entity));
        when(entrepreneurshipMapper.toResponse(entity)).thenReturn(response);

        List<EntrepreneurshipResponse> result = entrepreneurshipService.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void create_withValidRequest_shouldSave() {
        EntrepreneurshipRequest request = new EntrepreneurshipRequest();
        request.setCategoryId(1L);
        request.setUserId(1L);
        Category category = new Category();
        Entrepreneurship entity = new Entrepreneurship();
        EntrepreneurshipResponse response = EntrepreneurshipResponse.builder().entrepreneurshipId(1L).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(entrepreneurshipMapper.toEntity(request)).thenReturn(entity);
        when(entrepreneurshipRepository.save(entity)).thenReturn(entity);
        when(entrepreneurshipMapper.toResponse(entity)).thenReturn(response);

        EntrepreneurshipResponse result = entrepreneurshipService.create(request);
        assertThat(result.getEntrepreneurshipId()).isEqualTo(1L);
    }
}
