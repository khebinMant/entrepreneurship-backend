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
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepreneurshipServiceImpl implements EntrepreneurshipService {

    private final EntrepreneurshipRepository entrepreneurshipRepository;
    private final EntrepreneurshipQueryRepository entrepreneurshipQueryRepository;
    private final CategoryRepository categoryRepository;
    private final EntrepreneurshipMapper entrepreneurshipMapper;

    @Override
    public List<EntrepreneurshipResponse> findAll() {
        return entrepreneurshipRepository.findAll().stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();
    }

    @Override
    public EntrepreneurshipResponse findById(Long id) {
        return entrepreneurshipMapper.toResponse(
            entrepreneurshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", id)));
    }

    @Override
    public List<EntrepreneurshipResponse> findByUserId(Long userId) {
        return entrepreneurshipRepository.findByUserId(userId).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();
    }

    @Override
    public List<EntrepreneurshipResponse> search(String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        return entrepreneurshipQueryRepository.search(name, categoryId, isPhysical, isDigital).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public EntrepreneurshipResponse create(EntrepreneurshipRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        Entrepreneurship entity = entrepreneurshipMapper.toEntity(request);
        entity.setCategory(category);
        return entrepreneurshipMapper.toResponse(entrepreneurshipRepository.save(entity));
    }

    @Override
    @Transactional
    public EntrepreneurshipResponse update(Long id, EntrepreneurshipRequest request) {
        Entrepreneurship entity = entrepreneurshipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", id));
        entrepreneurshipMapper.updateEntityFromRequest(request, entity);
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            entity.setCategory(category);
        }
        return entrepreneurshipMapper.toResponse(entrepreneurshipRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!entrepreneurshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entrepreneurship", id);
        }
        entrepreneurshipRepository.deleteById(id);
    }
}
