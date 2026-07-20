package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkRequest;
import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkResponse;

import java.util.List;

public interface EntitySocialLinkService {
    List<EntitySocialLinkResponse> findByEntityId(Long entityId);
    EntitySocialLinkResponse findById(Long id);
    EntitySocialLinkResponse create(EntitySocialLinkRequest request);
    EntitySocialLinkResponse update(Long id, EntitySocialLinkRequest request);
    void delete(Long id);
}
