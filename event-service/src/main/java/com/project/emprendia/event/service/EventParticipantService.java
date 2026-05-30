package com.project.emprendia.event.service;

import com.project.emprendia.event.dto.EventParticipantResponse;

import java.util.List;

public interface EventParticipantService {
    List<EventParticipantResponse> findByEventId(Long eventId);
    List<EventParticipantResponse> findByEventIdAndStatus(Long eventId, Long statusId);
    EventParticipantResponse findById(Long id);
}

