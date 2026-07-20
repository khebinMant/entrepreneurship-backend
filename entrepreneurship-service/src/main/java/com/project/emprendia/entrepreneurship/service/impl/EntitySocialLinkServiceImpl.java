package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.domain.EntitySocialLink;
import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkRequest;
import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkResponse;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.repository.EntitySocialLinkRepository;
import com.project.emprendia.entrepreneurship.service.EntitySocialLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntitySocialLinkServiceImpl implements EntitySocialLinkService {

    private final EntitySocialLinkRepository socialLinkRepository;

    @Override
    public List<EntitySocialLinkResponse> findByEntityId(Long entityId) {
        return socialLinkRepository.findByEntityId(entityId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public EntitySocialLinkResponse findById(Long id) {
        return toResponse(socialLinkRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntitySocialLink", id)));
    }

    @Override
    @Transactional
    public EntitySocialLinkResponse create(EntitySocialLinkRequest request) {
        EntitySocialLink socialLink = EntitySocialLink.builder()
            .entityId(request.getEntityId())
            .socialPlatformId(request.getSocialPlatformId())
            .url(request.getUrl())
            .build();

        return toResponse(socialLinkRepository.save(socialLink));
    }

    @Override
    @Transactional
    public EntitySocialLinkResponse update(Long id, EntitySocialLinkRequest request) {
        EntitySocialLink socialLink = socialLinkRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntitySocialLink", id));

        socialLink.setSocialPlatformId(request.getSocialPlatformId());
        socialLink.setUrl(request.getUrl());

        return toResponse(socialLinkRepository.save(socialLink));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!socialLinkRepository.existsById(id)) {
            throw new ResourceNotFoundException("EntitySocialLink", id);
        }
        socialLinkRepository.deleteById(id);
    }

    private EntitySocialLinkResponse toResponse(EntitySocialLink socialLink) {
        return EntitySocialLinkResponse.builder()
            .socialLinkId(socialLink.getSocialLinkId())
            .entityId(socialLink.getEntityId())
            .socialPlatformId(socialLink.getSocialPlatformId())
            .url(socialLink.getUrl())
            .createdAt(socialLink.getCreatedAt())
            .build();
    }
}
