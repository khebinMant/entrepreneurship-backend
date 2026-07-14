package com.project.emprendia.event.service;

import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventResponse> findAll();

    EventResponse findById(Long id);

    List<EventResponse> findByCreator(Long userId);

    List<EventResponse> findByCreator(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                       LocalDateTime fromDate, LocalDateTime toDate);

    Page<EventResponse> findByCreatorPaginated(Long userId, String name, Long eventTypeId, Long eventVisibilityId,
                                                LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    List<EventResponse> search(String name, Long eventTypeId, Long eventVisibilityId,
                                LocalDateTime fromDate, LocalDateTime toDate);

    Page<EventResponse> searchPaginated(String name, Long eventTypeId, Long eventVisibilityId,
                                         LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

    EventResponse create(EventRequest request);

    EventResponse update(Long id, EventRequest request);

    void delete(Long id);
}
