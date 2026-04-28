package com.project.emprendia.event.service.impl;

import com.project.emprendia.event.domain.Event;
import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import com.project.emprendia.event.exception.ResourceNotFoundException;
import com.project.emprendia.event.mapping.mapper.EventMapper;
import com.project.emprendia.event.repository.EventQueryRepository;
import com.project.emprendia.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventRepository eventRepository;
    @Mock private EventQueryRepository eventQueryRepository;
    @Mock private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void findAll_shouldReturnMappedList() {
        Event event = new Event();
        EventResponse response = EventResponse.builder().eventId(1L).build();
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        List<EventResponse> result = eventService.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void findById_withInvalidId_shouldThrow() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.findById(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        EventRequest request = new EventRequest();
        Event entity = new Event();
        EventResponse response = EventResponse.builder().eventId(1L).build();

        when(eventMapper.toEntity(request)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(entity);
        when(eventMapper.toResponse(entity)).thenReturn(response);

        EventResponse result = eventService.create(request);
        assertThat(result.getEventId()).isEqualTo(1L);
    }
}
