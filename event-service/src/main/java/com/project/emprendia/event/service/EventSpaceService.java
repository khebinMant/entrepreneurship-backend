package com.project.emprendia.event.service;

import com.project.emprendia.event.dto.EventSpaceRequest;
import com.project.emprendia.event.dto.EventSpaceResponse;

import java.util.List;

public interface EventSpaceService {
    List<EventSpaceResponse> findByEventId(Long eventId);
    EventSpaceResponse findById(Long id);
    EventSpaceResponse create(EventSpaceRequest request);
    EventSpaceResponse update(Long id, EventSpaceRequest request);
    void delete(Long id);
}

