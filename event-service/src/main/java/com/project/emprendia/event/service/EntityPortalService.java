package com.project.emprendia.event.service;

import com.project.emprendia.event.dto.EntityPortalRequest;
import com.project.emprendia.event.dto.EntityPortalResponse;

public interface EntityPortalService {
    EntityPortalResponse findByEntityId(Long entityId);
    EntityPortalResponse findBySubdomain(String subdomain);
    EntityPortalResponse findById(Long id);
    EntityPortalResponse create(EntityPortalRequest request);
    EntityPortalResponse update(Long id, EntityPortalRequest request);
    void delete(Long id);
}
