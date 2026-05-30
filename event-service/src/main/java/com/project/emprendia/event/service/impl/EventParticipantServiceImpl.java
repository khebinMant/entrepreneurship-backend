package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.domain.EventEntrepreneurshipParticipant;
import com.project.emprendia.event.dto.EventParticipantResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EventEntrepreneurshipParticipantRepository;
import com.project.emprendia.event.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventEntrepreneurshipParticipantRepository participantRepository;

    @Override
    public List<EventParticipantResponse> findByEventId(Long eventId) {
        return participantRepository.findByEvent_EventId(eventId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<EventParticipantResponse> findByEventIdAndStatus(Long eventId, Long statusId) {
        if (statusId != null) {
            return participantRepository.findByEvent_EventIdAndParticipationStatusId(eventId, statusId)
                .stream()
                .map(this::toResponse)
                .toList();
        }
        return findByEventId(eventId);
    }

    @Override
    public EventParticipantResponse findById(Long id) {
        return toResponse(participantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventParticipant", id)));
    }

    private EventParticipantResponse toResponse(EventEntrepreneurshipParticipant participant) {
        return EventParticipantResponse.builder()
            .eventParticipantId(participant.getEventParticipantId())
            .eventId(participant.getEvent().getEventId())
            .eventName(participant.getEvent().getName())
            .entrepreneurshipId(participant.getEntrepreneurshipId())
            .spaceCode(participant.getSpaceCode())
            .participationStatusId(participant.getParticipationStatusId())
            .invitedAt(participant.getInvitedAt())
            .respondedAt(participant.getRespondedAt())
            .build();
    }
}

