package com.project.emprendia.event.service;

import com.project.emprendia.event.dto.BulkEventInvitationRequest;
import com.project.emprendia.event.dto.EventInvitationRequest;
import com.project.emprendia.event.dto.EventInvitationResponse;

import java.util.List;

public interface EventInvitationService {
    List<EventInvitationResponse> findByEventId(Long eventId);
    List<EventInvitationResponse> findByEventIdAndStatus(Long eventId, Long statusId);
    EventInvitationResponse findById(Long id);
    EventInvitationResponse create(EventInvitationRequest request);
    List<EventInvitationResponse> createBulk(BulkEventInvitationRequest request);
    EventInvitationResponse updateStatus(Long id, Long statusId, String message);
    void delete(Long id);
}

