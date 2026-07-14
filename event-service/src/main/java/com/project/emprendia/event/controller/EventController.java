package com.project.emprendia.event.controller;

import com.project.emprendia.event.dto.EventRequest;
import com.project.emprendia.event.dto.EventResponse;
import com.project.emprendia.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<?> findByCreator(
            @PathVariable Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long eventTypeId,
            @RequestParam(required = false) Long eventVisibilityId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        LocalDateTime fromDateTime = parseDateParam(fromDate, true);
        LocalDateTime toDateTime = parseDateParam(toDate, false);

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponse> result = eventService.findByCreatorPaginated(
                userId, name, eventTypeId, eventVisibilityId, fromDateTime, toDateTime, pageable);
            return ResponseEntity.ok(result);
        }

        List<EventResponse> result = eventService.findByCreator(
            userId, name, eventTypeId, eventVisibilityId, fromDateTime, toDateTime);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long eventTypeId,
            @RequestParam(required = false) Long eventVisibilityId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        LocalDateTime fromDateTime = parseDateParam(fromDate, true);
        LocalDateTime toDateTime = parseDateParam(toDate, false);

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<EventResponse> result = eventService.searchPaginated(
                name, eventTypeId, eventVisibilityId, fromDateTime, toDateTime, pageable);
            return ResponseEntity.ok(result);
        }

        List<EventResponse> result = eventService.search(name, eventTypeId, eventVisibilityId, fromDateTime, toDateTime);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LocalDateTime parseDateParam(String dateStr, boolean isFromDate) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
            return isFromDate ? date.atStartOfDay() : date.atTime(LocalTime.MAX);
        }
    }
}
