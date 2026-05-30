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
               description = "Obtiene participantes de un evento. Puede filtrar por estado: INVITED(1), ACCEPTED(2), REJECTED(3)")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventParticipantResponse>> findByEvent(
            @Parameter(description = "ID del evento") @PathVariable Long eventId,
            @Parameter(description = "ID del estado (opcional): 1=INVITED, 2=ACCEPTED, 3=REJECTED")
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

