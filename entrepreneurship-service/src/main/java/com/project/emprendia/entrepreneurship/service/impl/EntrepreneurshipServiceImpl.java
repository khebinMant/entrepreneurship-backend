package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.client.SharedServiceClient;
import com.project.emprendia.entrepreneurship.domain.Category;
import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import com.project.emprendia.entrepreneurship.dto.ImageGalleryResponse;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.mapping.mapper.EntrepreneurshipMapper;
import com.project.emprendia.entrepreneurship.repository.CategoryRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipQueryRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipRepository;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepreneurshipServiceImpl implements EntrepreneurshipService {

    private final EntrepreneurshipRepository entrepreneurshipRepository;
    private final EntrepreneurshipQueryRepository entrepreneurshipQueryRepository;
    private final CategoryRepository categoryRepository;
    private final EntrepreneurshipMapper entrepreneurshipMapper;
    private final SharedServiceClient sharedServiceClient;

    @Override
    public List<EntrepreneurshipResponse> findAll() {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipRepository.findAll().stream()
                .map(entrepreneurshipMapper::toResponse)
                .toList();

        // Enriquecer con imágenes
        entrepreneurships.forEach(this::enrichWithLogo);
        return entrepreneurships;
    }

    @Override
    public EntrepreneurshipResponse findById(Long id) {
        EntrepreneurshipResponse entrepreneurshipResponse =entrepreneurshipMapper.toResponse(
                entrepreneurshipRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", id)));

        // Enriquecer con imágenes
        enrichWithLogo(entrepreneurshipResponse);
        return entrepreneurshipResponse;
    }

    @Override
    public List<EntrepreneurshipResponse> findByUserId(Long userId) {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipRepository.findByUserId(userId).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();

        // Enriquecer con imágenes
        entrepreneurships.forEach(this::enrichWithLogo);

        return entrepreneurships;
    }

    @Override
    public List<EntrepreneurshipResponse> findByUserId(Long userId, String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipQueryRepository
            .findByUserId(userId, name, categoryId, isPhysical, isDigital).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();

        entrepreneurships.forEach(this::enrichWithLogo);

        return entrepreneurships;
    }

    @Override
    public Page<EntrepreneurshipResponse> findByUserIdPaginated(Long userId, String name, Long categoryId, Boolean isPhysical, Boolean isDigital, Pageable pageable) {
        Page<Entrepreneurship> page = entrepreneurshipQueryRepository
            .findByUserIdPaginated(userId, name, categoryId, isPhysical, isDigital, pageable);

        return page.map(entity -> {
            EntrepreneurshipResponse response = entrepreneurshipMapper.toResponse(entity);
            enrichWithLogo(response);
            return response;
        });
    }

    @Override
    public List<EntrepreneurshipResponse> search(String name, Long categoryId, Boolean isPhysical, Boolean isDigital) {
        List<EntrepreneurshipResponse> entrepreneurships = entrepreneurshipQueryRepository
            .search(name, categoryId, isPhysical, isDigital).stream()
            .map(entrepreneurshipMapper::toResponse)
            .toList();

        // Enriquecer con imágenes
        entrepreneurships.forEach(this::enrichWithLogo);

        return entrepreneurships;
    }

    @Override
    public Page<EntrepreneurshipResponse> searchPaginated(String name, Long categoryId, Boolean isPhysical, Boolean isDigital, Pageable pageable) {
        Page<Entrepreneurship> page = entrepreneurshipQueryRepository
            .searchPaginated(name, categoryId, isPhysical, isDigital, pageable);

        return page.map(entity -> {
            EntrepreneurshipResponse response = entrepreneurshipMapper.toResponse(entity);
            enrichWithLogo(response);
            return response;
        });
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

    /**
     * Enriquecer el response con la imagen logo del emprendimiento (displayOrder = 0)
     */
    private void enrichWithLogo(EntrepreneurshipResponse response) {
        try {
            List<ImageGalleryResponse> images = sharedServiceClient.getImagesForEntity(
                "ENTREPRENEURSHIP",
                response.getEntrepreneurshipId()
            );

            // Buscar la imagen con displayOrder = 0 (logo principal)
            images.stream()
                .filter(img -> img.getDisplayOrder() != null && img.getDisplayOrder() == 0)
                .findFirst()
                .ifPresent(logo -> {
                    response.setImageUrl(logo.getImageUrl());
                    response.setImageId(logo.getImageId());
                });

        } catch (Exception e) {
            log.warn("Error al obtener imagen para emprendimiento {}: {}",
                response.getEntrepreneurshipId(), e.getMessage());
            // No fallar si no se puede obtener la imagen
        }
    }
}
