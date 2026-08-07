package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.domain.EntityPortal;
import com.project.emprendia.event.dto.EntityPortalRequest;
import com.project.emprendia.event.dto.EntityPortalResponse;
import com.project.emprendia.event.exception.DuplicateResourceException;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EntityPortalRepository;
import com.project.emprendia.event.service.EntityPortalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntityPortalServiceImpl implements EntityPortalService {

    private final EntityPortalRepository portalRepository;

    @Override
    public EntityPortalResponse findByEntityId(Long entityId) {
        return portalRepository.findFirstByEntityIdOrderByPortalIdDesc(entityId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portal not found for entity: " + entityId));
    }

    @Override
    public EntityPortalResponse findBySubdomain(String subdomain) {
        return portalRepository.findBySubdomain(subdomain)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portal not found for subdomain: " + subdomain));
    }

    @Override
    public EntityPortalResponse findById(Long id) {
        return toResponse(portalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntityPortal", id)));
    }

    @Override
    @Transactional
    public EntityPortalResponse create(EntityPortalRequest request) {
        EntityPortal existing = portalRepository.findFirstByEntityIdOrderByPortalIdDesc(request.getEntityId())
            .orElse(null);

        if (existing == null && portalRepository.existsBySubdomain(request.getSubdomain())) {
            throw new DuplicateResourceException("Portal", "subdomain", request.getSubdomain());
        }

        if (existing != null) {
            existing.setSubdomain(request.getSubdomain());
            existing.setThemeId(request.getThemeId());
            if (request.getIsActive() != null) {
                existing.setIsActive(request.getIsActive());
            }
            if (request.getHtmlContent() != null) {
                existing.setHtmlContent(request.getHtmlContent());
            }
            return toResponse(portalRepository.save(existing));
        }

        EntityPortal portal = EntityPortal.builder()
            .entityId(request.getEntityId())
            .subdomain(request.getSubdomain())
            .themeId(request.getThemeId())
            .isActive(request.getIsActive() != null ? request.getIsActive() : true)
            .htmlContent(request.getHtmlContent())
            .build();

        return toResponse(portalRepository.save(portal));
    }

    @Override
    @Transactional
    public EntityPortalResponse update(Long id, EntityPortalRequest request) {
        EntityPortal portal = portalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntityPortal", id));

        if (!portal.getSubdomain().equals(request.getSubdomain()) &&
            portalRepository.existsBySubdomain(request.getSubdomain())) {
            throw new DuplicateResourceException("Portal", "subdomain", request.getSubdomain());
        }

        portal.setSubdomain(request.getSubdomain());
        portal.setThemeId(request.getThemeId());
        if (request.getIsActive() != null) {
            portal.setIsActive(request.getIsActive());
        }
        if (request.getHtmlContent() != null) {
            portal.setHtmlContent(request.getHtmlContent());
        }

        return toResponse(portalRepository.save(portal));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!portalRepository.existsById(id)) {
            throw new ResourceNotFoundException("EntityPortal", id);
        }
        portalRepository.deleteById(id);
    }

    private EntityPortalResponse toResponse(EntityPortal portal) {
        return EntityPortalResponse.builder()
            .portalId(portal.getPortalId())
            .entityId(portal.getEntityId())
            .subdomain(portal.getSubdomain())
            .themeId(portal.getThemeId())
            .isActive(portal.getIsActive())
            .htmlContent(portal.getHtmlContent())
            .createdAt(portal.getCreatedAt())
            .build();
    }
}
