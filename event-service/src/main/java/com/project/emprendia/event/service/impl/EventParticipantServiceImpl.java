package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.client.EntrepreneurshipServiceClient;
import com.project.emprendia.event.client.SharedServiceClient;
import com.project.emprendia.event.domain.EventEntrepreneurshipParticipant;
import com.project.emprendia.event.dto.EntrepreneurshipBasicResponse;
import com.project.emprendia.event.dto.EventParticipantResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EventEntrepreneurshipParticipantRepository;
import com.project.emprendia.event.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventEntrepreneurshipParticipantRepository participantRepository;
    private final EntrepreneurshipServiceClient entrepreneurshipClient;
    private final SharedServiceClient sharedServiceClient;

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
        EntrepreneurshipBasicResponse entrepreneurship;
        try {
            entrepreneurship = entrepreneurshipClient.getEntrepreneurshipById(participant.getEntrepreneurshipId());
            enrichWithImage(entrepreneurship);
        } catch (Exception e) {
            log.warn("Could not fetch entrepreneurship {} details: {}",
                participant.getEntrepreneurshipId(), e.getMessage());
            entrepreneurship = EntrepreneurshipBasicResponse.builder()
                .entrepreneurshipId(participant.getEntrepreneurshipId())
                .name("N/A")
                .build();
        }

        return EventParticipantResponse.builder()
            .eventParticipantId(participant.getEventParticipantId())
            .eventId(participant.getEvent().getEventId())
            .eventName(participant.getEvent().getName())
            .entrepreneurshipId(participant.getEntrepreneurshipId())
            .entrepreneurship(entrepreneurship)
            .spaceCode(participant.getSpaceCode())
            .participationStatusId(participant.getParticipationStatusId())
            .invitedAt(participant.getInvitedAt())
            .respondedAt(participant.getRespondedAt())
            .build();
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

