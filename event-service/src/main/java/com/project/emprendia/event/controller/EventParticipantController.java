package com.project.emprendia.event.controller;

import com.project.emprendia.event.dto.EventParticipantResponse;
import com.project.emprendia.event.service.EventParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event-participants")
@RequiredArgsConstructor
@Tag(name = "Event Participants", description = "Consulta de participantes confirmados en eventos")
public class EventParticipantController {

    private final EventParticipantService participantService;

    @Operation(summary = "Listar participantes por evento con filtro opcional de estado",
               description = "Obtiene participantes de un evento. Filtra por statusId del catálogo PARTICIPATION_STATUS (INVITED, ACCEPTED, REJECTED)")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventParticipantResponse>> findByEvent(
            @Parameter(description = "ID del evento") @PathVariable Long eventId,
            @Parameter(description = "statusId del catálogo PARTICIPATION_STATUS (usa GET /api/v1/catalogue-values/by-type/PARTICIPATION_STATUS)")
            @RequestParam(required = false) Long statusId) {
        return ResponseEntity.ok(participantService.findByEventIdAndStatus(eventId, statusId));
    }

    @Operation(summary = "Obtener participante por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventParticipantResponse> findById(
            @Parameter(description = "ID del participante") @PathVariable Long id) {
        return ResponseEntity.ok(participantService.findById(id));
    }
}

