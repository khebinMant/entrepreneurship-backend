package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.client.EntrepreneurshipServiceClient;
import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.domain.EventEntrepreneurshipParticipant;
import com.project.emprendia.event.domain.EventInvitation;
import com.project.emprendia.event.domain.EventSpace;
import com.project.emprendia.event.dto.EventInvitationRequest;
import com.project.emprendia.event.dto.EventInvitationResponse;
import com.project.emprendia.event.exception.DuplicateResourceException;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EventEntrepreneurshipParticipantRepository;
import com.project.emprendia.event.repository.EventInvitationRepository;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.repository.EventSpaceRepository;
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
    private final EventSpaceRepository eventSpaceRepository;
    private final EventEntrepreneurshipParticipantRepository participantRepository;
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
            throw new DuplicateResourceException("EventInvitation",
                "entrepreneurship_id in event",
                request.getEntrepreneurshipId().toString());
        }

        // Crear la invitación
        EventInvitation invitation = EventInvitation.builder()
            .event(event)
            .entrepreneurshipId(request.getEntrepreneurshipId())
            .invitationStatusId(request.getInvitationStatusId())
            .sentAt(LocalDateTime.now())
            .build();

        invitation = invitationRepository.save(invitation);

        // Crear el participante asociado con estado PENDIENTE
        EventEntrepreneurshipParticipant participant = EventEntrepreneurshipParticipant.builder()
            .event(event)
            .entrepreneurshipId(request.getEntrepreneurshipId())
            .participationStatusId(request.getInvitationStatusId()) // Mismo estado inicial
            .invitedAt(LocalDateTime.now())
            .build();

        participantRepository.save(participant);

        log.info("Invitation and participant created for entrepreneurship {} in event {}",
            request.getEntrepreneurshipId(), request.getEventId());

        return toResponse(invitation);
    }

    @Override
    @Transactional
    public EventInvitationResponse updateStatus(Long id, Long statusId) {
        EventInvitation invitation = invitationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventInvitation", id));

        // Capturar valores para usar en lambdas
        final Long eventId = invitation.getEvent().getEventId();
        final Long entrepreneurshipId = invitation.getEntrepreneurshipId();
        final Event event = invitation.getEvent();
        final LocalDateTime sentAt = invitation.getSentAt();

        // Actualizar invitación
        invitation.setInvitationStatusId(statusId);
        invitation.setRespondedAt(LocalDateTime.now());
        invitation = invitationRepository.save(invitation);

        // Buscar o crear participante si no existe (para invitaciones antiguas)
        EventEntrepreneurshipParticipant participant = participantRepository
            .findByEvent_EventIdAndEntrepreneurshipId(eventId, entrepreneurshipId)
            .orElseGet(() -> {
                log.warn("Participant not found for entrepreneurship {} in event {}. Creating new participant.",
                    entrepreneurshipId, eventId);
                return EventEntrepreneurshipParticipant.builder()
                    .event(event)
                    .entrepreneurshipId(entrepreneurshipId)
                    .participationStatusId(statusId)
                    .invitedAt(sentAt != null ? sentAt : LocalDateTime.now())
                    .build();
            });

        participant.setParticipationStatusId(statusId);
        participant.setRespondedAt(LocalDateTime.now());

        // Si fue ACEPTADO, crear y asignar espacio
        if (statusId == 2) { // ACCEPTED (ajusta el ID según tu catálogo)
            String spaceCode = generateSpaceCode(eventId);

            EventSpace space = EventSpace.builder()
                .event(invitation.getEvent())
                .spaceCode(spaceCode)
                .isAvailable(false) // Ya no está disponible porque fue asignado
                .build();

            space = eventSpaceRepository.save(space);
            participant.setSpaceCode(spaceCode);

            log.info("Space {} created and assigned to entrepreneurship {} for event {}",
                spaceCode, entrepreneurshipId, eventId);
        }

        participantRepository.save(participant);

        log.info("Invitation {} updated to status {}. Participant also updated.", id, statusId);

        return toResponse(invitation);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EventInvitation invitation = invitationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventInvitation", id));

        // Eliminar participante asociado si existe
        participantRepository.findByEvent_EventIdAndEntrepreneurshipId(
            invitation.getEvent().getEventId(),
            invitation.getEntrepreneurshipId())
            .ifPresent(participantRepository::delete);

        invitationRepository.deleteById(id);

        log.info("Invitation {} and associated participant deleted", id);
    }

    /**
     * Genera un código único de espacio para el evento
     * Formato: A-01, A-02, ..., A-99, B-01, etc.
     */
    private String generateSpaceCode(Long eventId) {
        long count = eventSpaceRepository.findByEvent_EventId(eventId).size();
        int number = (int) (count % 99) + 1;
        char letter = (char) ('A' + (count / 99));
        return String.format("%c-%02d", letter, number);
    }

    private EventInvitationResponse toResponse(EventInvitation invitation) {
        return EventInvitationResponse.builder()
            .invitationId(invitation.getInvitationId())
            .eventId(invitation.getEvent().getEventId())
            .eventName(invitation.getEvent().getName())
            .entrepreneurshipId(invitation.getEntrepreneurshipId())
            .eventSpaceId(invitation.getEventSpace() != null ? invitation.getEventSpace().getEventSpaceId() : null)
            .invitationStatusId(invitation.getInvitationStatusId())
            .sentAt(invitation.getSentAt())
            .respondedAt(invitation.getRespondedAt())
            .build();
    }
}

