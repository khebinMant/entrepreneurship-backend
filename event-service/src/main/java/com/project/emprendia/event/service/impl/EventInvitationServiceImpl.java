package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.client.EntrepreneurshipServiceClient;
import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.domain.EventInvitation;
import com.project.emprendia.event.dto.EventInvitationRequest;
import com.project.emprendia.event.dto.EventInvitationResponse;
import com.project.emprendia.event.exception.DuplicateResourceException;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EventInvitationRepository;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.service.EventInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventInvitationServiceImpl implements EventInvitationService {

    private final EventInvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final EntrepreneurshipServiceClient entrepreneurshipClient;

    @Override
    public List<EventInvitationResponse> findByEventId(Long eventId) {
        return invitationRepository.findByEvent_EventId(eventId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public List<EventInvitationResponse> findByEventIdAndStatus(Long eventId, Long statusId) {
        if (statusId != null) {
            return invitationRepository.findByEvent_EventIdAndInvitationStatusId(eventId, statusId)
                .stream()
                .map(this::toResponse)
                .toList();
        }
        return findByEventId(eventId);
    }

    @Override
    public EventInvitationResponse findById(Long id) {
        return toResponse(invitationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventInvitation", id)));
    }

    @Override
    @Transactional
    public EventInvitationResponse create(EventInvitationRequest request) {
        // Validar que el evento existe
        Event event = eventRepository.findById(request.getEventId())
            .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));

        // Validar que el emprendimiento existe llamando al microservicio
        try {
            entrepreneurshipClient.getEntrepreneurshipById(request.getEntrepreneurshipId());
        } catch (Exception e) {
            log.error("Entrepreneurship not found: {}", request.getEntrepreneurshipId());
            throw new ResourceNotFoundException("Entrepreneurship", request.getEntrepreneurshipId());
        }

        // Validar que no existe invitación duplicada
        if (invitationRepository.existsByEvent_EventIdAndEntrepreneurshipId(
                request.getEventId(), request.getEntrepreneurshipId())) {
            throw new DuplicateResourceException("Invitation already exists for this entrepreneurship in this event");
        }

        EventInvitation invitation = EventInvitation.builder()
            .event(event)
            .entrepreneurshipId(request.getEntrepreneurshipId())
            .eventSpaceId(request.getEventSpaceId())
            .invitationStatusId(request.getInvitationStatusId())
            .sentAt(LocalDateTime.now())
            .build();

        return toResponse(invitationRepository.save(invitation));
    }

    @Override
    @Transactional
    public EventInvitationResponse updateStatus(Long id, Long statusId) {
        EventInvitation invitation = invitationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventInvitation", id));

        invitation.setInvitationStatusId(statusId);
        invitation.setRespondedAt(LocalDateTime.now());

        return toResponse(invitationRepository.save(invitation));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!invitationRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventInvitation", id);
        }
        invitationRepository.deleteById(id);
    }

    private EventInvitationResponse toResponse(EventInvitation invitation) {
        return EventInvitationResponse.builder()
            .invitationId(invitation.getInvitationId())
            .eventId(invitation.getEvent().getEventId())
            .eventName(invitation.getEvent().getName())
            .entrepreneurshipId(invitation.getEntrepreneurshipId())
            .eventSpaceId(invitation.getEventSpaceId())
            .invitationStatusId(invitation.getInvitationStatusId())
            .sentAt(invitation.getSentAt())
            .respondedAt(invitation.getRespondedAt())
            .build();
    }
}

