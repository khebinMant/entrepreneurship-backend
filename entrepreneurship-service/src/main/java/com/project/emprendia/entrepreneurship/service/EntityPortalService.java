package com.project.emprendia.entrepreneurship.service;

import com.project.emprendia.entrepreneurship.dto.EntityPortalRequest;
import com.project.emprendia.entrepreneurship.dto.EntityPortalResponse;

public interface EntityPortalService {
    EntityPortalResponse findByEntityId(Long entityId);
    EntityPortalResponse findById(Long id);
    EntityPortalResponse create(EntityPortalRequest request);
    EntityPortalResponse update(Long id, EntityPortalRequest request);
    void delete(Long id);
}
