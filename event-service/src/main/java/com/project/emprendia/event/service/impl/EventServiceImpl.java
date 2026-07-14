package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.client.SharedServiceClient;
import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.mapping.mapper.EventMapper;
import com.project.emprendia.event.repository.EventQueryRepository;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final EventMapper eventMapper;
    private final SharedServiceClient sharedServiceClient;

    @Override
    public List<EventResponse> findAll() {
        List<EventResponse> events = eventRepository.findAll().stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichWithCoverImage);
        return events;
    }

    @Override
    public EventResponse findById(Long id) {
        EventResponse response = eventMapper.toResponse(
            eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id)));
        enrichWithCoverImage(response);
        return response;
    }

    @Override
    public List<EventResponse> findByCreator(Long userId) {
        List<EventResponse> events = eventRepository.findByCreatedByUserId(userId).stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichWithCoverImage);
        return events;
    }

    @Override
    public List<EventResponse> findByCreator(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                              LocalDateTime fromDate, LocalDateTime toDate) {
        List<EventResponse> events = eventQueryRepository
            .findByCreator(userId, name, eventTypeId, eventVisibilityId, fromDate, toDate).stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichWithCoverImage);
        return events;
    }

    @Override
    public Page<EventResponse> findByCreatorPaginated(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                                       LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Page<Event> page = eventQueryRepository.findByCreatorPaginated(
            userId, name, eventTypeId, eventVisibilityId, fromDate, toDate, pageable);

        return page.map(entity -> {
            EventResponse response = eventMapper.toResponse(entity);
            enrichWithCoverImage(response);
            return response;
        });
    }

    @Override
    public List<EventResponse> search(String name, Long eventTypeId, Long eventVisibilityId,
                                       LocalDateTime fromDate, LocalDateTime toDate) {
        List<EventResponse> events = eventQueryRepository
            .search(name, eventTypeId, eventVisibilityId, fromDate, toDate).stream()
            .map(eventMapper::toResponse)
            .toList();

        events.forEach(this::enrichWithCoverImage);
        return events;
    }

    @Override
    public Page<EventResponse> searchPaginated(String name, Long eventTypeId, Long eventVisibilityId,
                                                LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Page<Event> page = eventQueryRepository.searchPaginated(
            name, eventTypeId, eventVisibilityId, fromDate, toDate, pageable);

        return page.map(entity -> {
            EventResponse response = eventMapper.toResponse(entity);
            enrichWithCoverImage(response);
            return response;
        });
    }

    @Override
    @Transactional
    public EventResponse create(EventRequest request) {
        Event entity = eventMapper.toEntity(request);
        return eventMapper.toResponse(eventRepository.save(entity));
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event entity = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        eventMapper.updateEntityFromRequest(request, entity);
        return eventMapper.toResponse(eventRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }
        eventRepository.deleteById(id);
    }

    /**
     * Enriquecer el response con la imagen de portada del evento (displayOrder = 0)
     */
    private void enrichWithCoverImage(EventResponse response) {
        try {
            List<Map<String, Object>> images = sharedServiceClient.getImagesForEntity(
                "EVENT",
                response.getEventId()
            );

            // Buscar la imagen con displayOrder = 0 (portada principal)
            images.stream()
                .filter(img -> {
                    Object displayOrder = img.get("displayOrder");
                    return displayOrder != null &&
                           (displayOrder instanceof Integer && (Integer) displayOrder == 0);
                })
                .findFirst()
                .ifPresent(coverImage -> {
                    response.setImageUrl((String) coverImage.get("imageUrl"));
                    Object imageId = coverImage.get("imageId");
                    if (imageId instanceof Number) {
                        response.setImageId(((Number) imageId).longValue());
                    }
                });

        } catch (Exception e) {
            log.warn("Error al obtener imagen para evento {}: {}",
                response.getEventId(), e.getMessage());
            // No fallar si no se puede obtener la imagen
        }
    }
}
