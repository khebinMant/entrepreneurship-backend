package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipPortal;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipPortalRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipPortalResponse;
import com.project.emprendia.entrepreneurship.exception.DuplicateResourceException;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipPortalRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipRepository;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipPortalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepreneurshipPortalServiceImpl implements EntrepreneurshipPortalService {

    private final EntrepreneurshipPortalRepository portalRepository;
    private final EntrepreneurshipRepository entrepreneurshipRepository;

    @Override
    public EntrepreneurshipPortalResponse findByEntrepreneurshipId(Long entrepreneurshipId) {
        return portalRepository.findByEntrepreneurship_EntrepreneurshipId(entrepreneurshipId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portal not found for entrepreneurship: " + entrepreneurshipId));
    }

    @Override
    public EntrepreneurshipPortalResponse findById(Long id) {
        return toResponse(portalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntrepreneurshipPortal", id)));
    }

    @Override
    @Transactional
    public EntrepreneurshipPortalResponse create(EntrepreneurshipPortalRequest request) {
        Entrepreneurship entrepreneurship = entrepreneurshipRepository
            .findById(request.getEntrepreneurshipId())
            .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", request.getEntrepreneurshipId()));

        if (portalRepository.existsBySubdomain(request.getSubdomain())) {
            throw new DuplicateResourceException("Portal", "subdomain", request.getSubdomain());
        }

        EntrepreneurshipPortal portal = EntrepreneurshipPortal.builder()
            .entrepreneurship(entrepreneurship)
            .subdomain(request.getSubdomain())
            .themeId(request.getThemeId())
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .build();

        return toResponse(portalRepository.save(portal));
    }

    @Override
    @Transactional
    public EntrepreneurshipPortalResponse update(Long id, EntrepreneurshipPortalRequest request) {
        EntrepreneurshipPortal portal = portalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntrepreneurshipPortal", id));

        if (!portal.getSubdomain().equals(request.getSubdomain()) &&
            portalRepository.existsBySubdomain(request.getSubdomain())) {
            throw new DuplicateResourceException("Portal", "subdomain", request.getSubdomain());
        }

        portal.setSubdomain(request.getSubdomain());
        portal.setThemeId(request.getThemeId());
        if (request.getIsActive() != null) {
            portal.setIsActive(request.getIsActive());
        }

        return toResponse(portalRepository.save(portal));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!portalRepository.existsById(id)) {
            throw new ResourceNotFoundException("EntrepreneurshipPortal", id);
        }
        portalRepository.deleteById(id);
    }

    private EntrepreneurshipPortalResponse toResponse(EntrepreneurshipPortal portal) {
        return EntrepreneurshipPortalResponse.builder()
            .portalId(portal.getPortalId())
            .entrepreneurshipId(portal.getEntrepreneurship().getEntrepreneurshipId())
            .entrepreneurshipName(portal.getEntrepreneurship().getName())
            .subdomain(portal.getSubdomain())
            .themeId(portal.getThemeId())
            .isActive(portal.getIsActive())
            .createdAt(portal.getCreatedAt())
            .build();
    }
}

