package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.client.EntrepreneurshipServiceClient;
import com.project.emprendia.event.client.SharedServiceClient;
import com.project.emprendia.event.client.UserServiceClient;
import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.domain.EventEntrepreneurshipParticipant;
import com.project.emprendia.event.domain.EventInvitation;
import com.project.emprendia.event.domain.EventSpace;
import com.project.emprendia.event.dto.BulkEventInvitationRequest;
import com.project.emprendia.event.dto.CatalogueValueResponse;
import com.project.emprendia.event.dto.EntrepreneurshipBasicResponse;
import com.project.emprendia.event.dto.EventInvitationRequest;
import com.project.emprendia.event.dto.EventInvitationResponse;
import com.project.emprendia.event.exception.DuplicateResourceException;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EventEntrepreneurshipParticipantRepository;
import com.project.emprendia.event.repository.EventInvitationRepository;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.repository.EventSpaceRepository;
import com.project.emprendia.event.service.CatalogueService;
import com.project.emprendia.event.service.EmailService;
import com.project.emprendia.event.service.EventInvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final SharedServiceClient sharedServiceClient;
    private final UserServiceClient userServiceClient;
    private final CatalogueService catalogueService;
    private final EmailService emailService;

    @Value("${mail.from}")
    private String mailFrom;

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
        CreateResult result = doCreateItem(request);
        sendInvitationEmail(result.request, result.event,
            result.entrepreneurship.getName(), result.entrepreneurship, result.invitation.getInvitationId());
        return toResponse(result.invitation);
    }

    @Override
    @Transactional
    public List<EventInvitationResponse> createBulk(BulkEventInvitationRequest request) {
        List<EventInvitationResponse> responses = new ArrayList<>();
        List<CreateResult> created = new ArrayList<>();

        for (EventInvitationRequest item : request.getInvitations()) {
            try {
                CreateResult result = doCreateItem(item);
                created.add(result);
                responses.add(toResponse(result.invitation));
            } catch (Exception e) {
                log.error("Error al crear invitación para entrepreneurship {} en evento {}: {}",
                    item.getEntrepreneurshipId(), item.getEventId(), e.getMessage());
            }
        }

        for (CreateResult result : created) {
            try {
                sendInvitationEmail(result.request, result.event,
                    result.entrepreneurship.getName(), result.entrepreneurship, result.invitation.getInvitationId());
            } catch (Exception e) {
                log.error("Error al enviar correo para entrepreneurship {} en evento {}: {}",
                    result.request.getEntrepreneurshipId(), result.request.getEventId(), e.getMessage());
            }
        }

        return responses;
    }

    @Transactional
    protected CreateResult doCreateItem(EventInvitationRequest request) {
        Event event = eventRepository.findById(request.getEventId())
            .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));

        EntrepreneurshipBasicResponse entrepreneurship;
        try {
            entrepreneurship = entrepreneurshipClient.getEntrepreneurshipById(request.getEntrepreneurshipId());
        } catch (Exception e) {
            log.error("Entrepreneurship not found: {}", request.getEntrepreneurshipId());
            throw new ResourceNotFoundException("Entrepreneurship", request.getEntrepreneurshipId());
        }

        if (invitationRepository.existsByEvent_EventIdAndEntrepreneurshipId(
                request.getEventId(), request.getEntrepreneurshipId())) {
            throw new DuplicateResourceException("EventInvitation",
                "entrepreneurship_id in event",
                request.getEntrepreneurshipId().toString());
        }

        EventInvitation invitation = EventInvitation.builder()
            .event(event)
            .entrepreneurshipId(request.getEntrepreneurshipId())
            .invitationStatusId(request.getInvitationStatusId())
            .sentAt(LocalDateTime.now())
            .build();

        invitation = invitationRepository.save(invitation);

        Long invitedStatusId = catalogueService.getStatusId("EVENT_PARTICIPATION_STATUS", "INVITED");
        EventEntrepreneurshipParticipant participant = EventEntrepreneurshipParticipant.builder()
            .event(event)
            .entrepreneurshipId(request.getEntrepreneurshipId())
            .participationStatusId(invitedStatusId)
            .invitedAt(LocalDateTime.now())
            .build();

        participantRepository.save(participant);

        log.info("Invitation and participant created for entrepreneurship {} in event {}",
            request.getEntrepreneurshipId(), request.getEventId());

        return new CreateResult(request, event, entrepreneurship, invitation);
    }

    private record CreateResult(EventInvitationRequest request, Event event,
                                EntrepreneurshipBasicResponse entrepreneurship, EventInvitation invitation) {}

    private void sendInvitationEmail(EventInvitationRequest request, Event event, String entrepreneurshipName,
                                      EntrepreneurshipBasicResponse entrepreneurship, Long invitationId) {
        try {
            String recipientEmail = request.getEmail();
            if (recipientEmail == null || recipientEmail.isBlank()) {
                recipientEmail = resolveEmail(request.getEntrepreneurshipId(), entrepreneurship);
            }

            if (recipientEmail == null || recipientEmail.isBlank()) {
                log.warn("No se pudo resolver email para entrepreneurship {} en evento {}",
                    request.getEntrepreneurshipId(), request.getEventId());
                return;
            }

            String eventDate = event.getStartDatetime() != null
                ? event.getStartDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null;

            emailService.sendInvitationEmail(
                recipientEmail,
                event.getName(),
                event.getDescription(),
                eventDate,
                entrepreneurshipName,
                request.getMessage(),
                invitationId
            );
        } catch (Exception e) {
            log.error("Error al enviar correo de invitación para entrepreneurship {} en evento {}: {}",
                request.getEntrepreneurshipId(), request.getEventId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public EventInvitationResponse updateStatus(Long id, Long statusId, String message) {
        EventInvitation invitation = invitationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventInvitation", id));

        final Long eventId = invitation.getEvent().getEventId();
        final Long entrepreneurshipId = invitation.getEntrepreneurshipId();
        final Event event = invitation.getEvent();
        final LocalDateTime sentAt = invitation.getSentAt();

        // Actualizar invitación
        invitation.setInvitationStatusId(statusId);
        invitation.setRespondedAt(LocalDateTime.now());
        invitation = invitationRepository.save(invitation);

        // Resolver el status de participante correspondiente
        Long participantStatusId = resolveParticipantStatus(statusId);

        // Buscar o crear participante si no existe (para invitaciones antiguas)
        EventEntrepreneurshipParticipant participant = participantRepository
            .findByEvent_EventIdAndEntrepreneurshipId(eventId, entrepreneurshipId)
            .orElseGet(() -> {
                log.warn("Participant not found for entrepreneurship {} in event {}. Creating new participant.",
                    entrepreneurshipId, eventId);
                return EventEntrepreneurshipParticipant.builder()
                    .event(event)
                    .entrepreneurshipId(entrepreneurshipId)
                    .participationStatusId(participantStatusId)
                    .invitedAt(sentAt != null ? sentAt : LocalDateTime.now())
                    .build();
            });

        participant.setParticipationStatusId(participantStatusId);
        participant.setRespondedAt(LocalDateTime.now());

        // Si fue ACEPTADO, crear y asignar espacio
        Long acceptedStatusId = catalogueService.getStatusId("INVITATION_STATUS", "ACCEPTED");
        if (statusId.equals(acceptedStatusId)) {
            String spaceCode = generateSpaceCode(eventId);

            EventSpace space = EventSpace.builder()
                .event(invitation.getEvent())
                .spaceCode(spaceCode)
                .isAvailable(false)
                .build();

            space = eventSpaceRepository.save(space);
            participant.setSpaceCode(spaceCode);

            log.info("Space {} created and assigned to entrepreneurship {} for event {}",
                spaceCode, entrepreneurshipId, eventId);
        }

        participantRepository.save(participant);

        // Si fue reenviado a PENDING, actualizar fecha y reenviar correo
        Long pendingStatusId = catalogueService.getStatusId("INVITATION_STATUS", "PENDING");
        if (statusId.equals(pendingStatusId)) {
            invitation.setSentAt(LocalDateTime.now());
            invitationRepository.save(invitation);
            String resendMessage = (message != null && !message.isBlank()) ? message : "Te recordamos que tienes una invitacion pendiente para este evento.";
            resendInvitationEmail(invitation, resendMessage);
        }

        // Si fue RECHAZADO con mensaje, notificar al emprendimiento
        Long rejectedStatusId = catalogueService.getStatusId("INVITATION_STATUS", "REJECTED");
        if (statusId.equals(rejectedStatusId) && message != null && !message.isBlank()) {
            sendRejectionNotification(invitation, message);
        }

        log.info("Invitation {} updated to status {}. Participant also updated.", id, statusId);

        return toResponse(invitation);
    }

    private void sendRejectionNotification(EventInvitation invitation, String reason) {
        try {
            EntrepreneurshipBasicResponse entrepreneurship = fetchEntrepreneurship(invitation.getEntrepreneurshipId());
            Event event = invitation.getEvent();
            String recipientEmail = resolveEmail(invitation.getEntrepreneurshipId(), entrepreneurship);
            if (recipientEmail == null || recipientEmail.isBlank()) return;

            emailService.sendRejectionEmail(
                recipientEmail,
                event.getName(),
                entrepreneurship.getName(),
                reason
            );
        } catch (Exception e) {
            log.error("Error al enviar notificación de rechazo para invitación {}: {}",
                invitation.getInvitationId(), e.getMessage());
        }
    }

    private void resendInvitationEmail(EventInvitation invitation, String message) {
        try {
            EntrepreneurshipBasicResponse entrepreneurship = fetchEntrepreneurship(invitation.getEntrepreneurshipId());
            Event event = invitation.getEvent();
            String recipientEmail = resolveEmail(invitation.getEntrepreneurshipId(), entrepreneurship);
            if (recipientEmail == null || recipientEmail.isBlank()) return;

            String eventDate = event.getStartDatetime() != null
                ? event.getStartDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : null;

            emailService.sendInvitationEmail(
                recipientEmail,
                event.getName(),
                event.getDescription(),
                eventDate,
                entrepreneurship.getName(),
                message,
                invitation.getInvitationId()
            );
        } catch (Exception e) {
            log.error("Error al reenviar correo para invitación {}: {}", invitation.getInvitationId(), e.getMessage());
        }
    }

    private String resolveEmail(Long entrepreneurshipId, EntrepreneurshipBasicResponse entrepreneurship) {
        Map<String, String> emailResponse = userServiceClient.getUserEmail(entrepreneurship.getUserId());
        String email = emailResponse.get("email");
        if (email == null || email.isBlank()) {
            log.warn("No se pudo resolver email para entrepreneurship {} (userId={})",
                entrepreneurshipId, entrepreneurship.getUserId());
        }
        return email;
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

    private Long resolveParticipantStatus(Long invitationStatusId) {
        try {
            CatalogueValueResponse value = sharedServiceClient.getValueById(invitationStatusId);
            String code = value.getCode();
            if ("PENDING".equals(code)) {
                code = "INVITED";
            }
            return catalogueService.getStatusId("EVENT_PARTICIPATION_STATUS", code);
        } catch (Exception e) {
            log.warn("Could not resolve participant status from invitation status {}: {}", invitationStatusId, e.getMessage());
            return invitationStatusId;
        }
    }

    private EventInvitationResponse toResponse(EventInvitation invitation) {
        EntrepreneurshipBasicResponse entrepreneurship = fetchEntrepreneurship(invitation.getEntrepreneurshipId());

        return EventInvitationResponse.builder()
            .invitationId(invitation.getInvitationId())
            .eventId(invitation.getEvent().getEventId())
            .eventName(invitation.getEvent().getName())
            .entrepreneurshipId(invitation.getEntrepreneurshipId())
            .entrepreneurship(entrepreneurship)
            .eventSpaceId(invitation.getEventSpace() != null ? invitation.getEventSpace().getEventSpaceId() : null)
            .invitationStatusId(invitation.getInvitationStatusId())
            .sentAt(invitation.getSentAt())
            .respondedAt(invitation.getRespondedAt())
            .build();
    }

    private EntrepreneurshipBasicResponse fetchEntrepreneurship(Long entrepreneurshipId) {
        try {
            EntrepreneurshipBasicResponse entrepreneurship = entrepreneurshipClient.getEntrepreneurshipById(entrepreneurshipId);
            enrichWithImage(entrepreneurship);
            return entrepreneurship;
        } catch (Exception e) {
            log.warn("Could not fetch entrepreneurship {} details: {}", entrepreneurshipId, e.getMessage());
            return EntrepreneurshipBasicResponse.builder()
                .entrepreneurshipId(entrepreneurshipId)
                .name("N/A")
                .build();
        }
    }

    private void enrichWithImage(EntrepreneurshipBasicResponse entrepreneurship) {
        try {
            List<Map<String, Object>> images = sharedServiceClient.getImagesForEntity(
                "ENTREPRENEURSHIP",
                entrepreneurship.getEntrepreneurshipId()
            );
            images.stream()
                .filter(img -> {
                    Object displayOrder = img.get("displayOrder");
                    return displayOrder != null &&
                        (displayOrder instanceof Integer && (Integer) displayOrder == 0);
                })
                .findFirst()
                .ifPresent(logo -> {
                    entrepreneurship.setImageUrl((String) logo.get("imageUrl"));
                    Object imageId = logo.get("imageId");
                    if (imageId instanceof Number) {
                        entrepreneurship.setImageId(((Number) imageId).longValue());
                    }
                });
        } catch (Exception e) {
            log.warn("Error al obtener imagen para emprendimiento {}: {}",
                entrepreneurship.getEntrepreneurshipId(), e.getMessage());
        }
    }
}

