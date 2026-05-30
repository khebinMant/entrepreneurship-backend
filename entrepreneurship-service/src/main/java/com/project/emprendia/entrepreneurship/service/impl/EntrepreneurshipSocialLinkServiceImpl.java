package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipSocialLink;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipSocialLinkRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipSocialLinkResponse;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipSocialLinkRepository;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipSocialLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepreneurshipSocialLinkServiceImpl implements EntrepreneurshipSocialLinkService {

    private final EntrepreneurshipSocialLinkRepository socialLinkRepository;
    private final EntrepreneurshipRepository entrepreneurshipRepository;

    @Override
    public List<EntrepreneurshipSocialLinkResponse> findByEntrepreneurshipId(Long entrepreneurshipId) {
        return socialLinkRepository.findByEntrepreneurshipEntrepreneurshipId(entrepreneurshipId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public EntrepreneurshipSocialLinkResponse findById(Long id) {
        return toResponse(socialLinkRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntrepreneurshipSocialLink", id)));
    }

    @Override
    @Transactional
    public EntrepreneurshipSocialLinkResponse create(EntrepreneurshipSocialLinkRequest request) {
        Entrepreneurship entrepreneurship = entrepreneurshipRepository
            .findById(request.getEntrepreneurshipId())
            .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", request.getEntrepreneurshipId()));

        EntrepreneurshipSocialLink socialLink = EntrepreneurshipSocialLink.builder()
            .entrepreneurship(entrepreneurship)
            .socialPlatformId(request.getSocialPlatformId())
            .url(request.getUrl())
            .build();

        return toResponse(socialLinkRepository.save(socialLink));
    }

    @Override
    @Transactional
    public EntrepreneurshipSocialLinkResponse update(Long id, EntrepreneurshipSocialLinkRequest request) {
        EntrepreneurshipSocialLink socialLink = socialLinkRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntrepreneurshipSocialLink", id));

        socialLink.setSocialPlatformId(request.getSocialPlatformId());
        socialLink.setUrl(request.getUrl());

        return toResponse(socialLinkRepository.save(socialLink));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!socialLinkRepository.existsById(id)) {
            throw new ResourceNotFoundException("EntrepreneurshipSocialLink", id);
        }
        socialLinkRepository.deleteById(id);
    }

    private EntrepreneurshipSocialLinkResponse toResponse(EntrepreneurshipSocialLink socialLink) {
        return EntrepreneurshipSocialLinkResponse.builder()
            .socialLinkId(socialLink.getSocialLinkId())
            .entrepreneurshipId(socialLink.getEntrepreneurship().getEntrepreneurshipId())
            .entrepreneurshipName(socialLink.getEntrepreneurship().getName())
            .socialPlatformId(socialLink.getSocialPlatformId())
            .url(socialLink.getUrl())
            .createdAt(socialLink.getCreatedAt())
            .build();
    }
}

