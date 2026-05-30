package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.domain.EventSpace;
import com.project.emprendia.event.dto.EventSpaceRequest;
import com.project.emprendia.event.dto.EventSpaceResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.repository.EventSpaceRepository;
import com.project.emprendia.event.service.EventSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventSpaceServiceImpl implements EventSpaceService {

    private final EventSpaceRepository spaceRepository;
    private final EventRepository eventRepository;

    @Override
    public List<EventSpaceResponse> findByEventId(Long eventId) {
        return spaceRepository.findByEvent_EventId(eventId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public EventSpaceResponse findById(Long id) {
        return toResponse(spaceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventSpace", id)));
    }

    @Override
    @Transactional
    public EventSpaceResponse create(EventSpaceRequest request) {
        Event event = eventRepository.findById(request.getEventId())
            .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));

        EventSpace space = EventSpace.builder()
            .event(event)
            .spaceCode(request.getSpaceCode())
            .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
            .build();

        return toResponse(spaceRepository.save(space));
    }

    @Override
    @Transactional
    public EventSpaceResponse update(Long id, EventSpaceRequest request) {
        EventSpace space = spaceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EventSpace", id));

        space.setSpaceCode(request.getSpaceCode());
        if (request.getIsAvailable() != null) {
            space.setIsAvailable(request.getIsAvailable());
        }

        return toResponse(spaceRepository.save(space));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventSpace", id);
        }
        spaceRepository.deleteById(id);
    }

    private EventSpaceResponse toResponse(EventSpace space) {
        return EventSpaceResponse.builder()
            .eventSpaceId(space.getEventSpaceId())
            .eventId(space.getEvent().getEventId())
            .eventName(space.getEvent().getName())
            .spaceCode(space.getSpaceCode())
            .isAvailable(space.getIsAvailable())
            .createdAt(space.getCreatedAt())
            .build();
    }
}

