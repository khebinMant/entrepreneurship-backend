package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.mapping.mapper.EventMapper;
import com.project.emprendia.event.repository.EventQueryRepository;
import com.project.emprendia.event.repository.EventRepository;
import com.project.emprendia.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream()
            .map(eventMapper::toResponse)
            .toList();
    }

    @Override
    public EventResponse findById(Long id) {
        return eventMapper.toResponse(
            eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id)));
    }

    @Override
    public List<EventResponse> findByCreator(Long userId) {
        return eventRepository.findByCreatedByUserId(userId).stream()
            .map(eventMapper::toResponse)
            .toList();
    }

    @Override
    public List<EventResponse> search(String name, Long eventTypeId, Long eventVisibilityId,
                                       LocalDateTime fromDate, LocalDateTime toDate) {
        return eventQueryRepository.search(name, eventTypeId, eventVisibilityId, fromDate, toDate).stream()
            .map(eventMapper::toResponse)
            .toList();
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
}
