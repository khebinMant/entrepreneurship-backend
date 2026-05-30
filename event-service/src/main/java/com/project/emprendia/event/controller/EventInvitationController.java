package com.project.emprendia.event.controller;

import com.project.emprendia.event.dto.EventInvitationRequest;
import com.project.emprendia.event.dto.EventInvitationResponse;
import com.project.emprendia.event.service.EventInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event-invitations")
@RequiredArgsConstructor
@Tag(name = "Event Invitations", description = "Gestión de invitaciones a emprendimientos para participar en eventos")
public class EventInvitationController {

    private final EventInvitationService invitationService;

    @Operation(summary = "Listar invitaciones por evento con filtro opcional de estado",
               description = "Obtiene invitaciones de un evento. Puede filtrar por estado: PENDING(1), ACCEPTED(2), REJECTED(3)")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventInvitationResponse>> findByEvent(
            @Parameter(description = "ID del evento") @PathVariable Long eventId,
            @Parameter(description = "ID del estado (opcional): 1=PENDING, 2=ACCEPTED, 3=REJECTED")
            @RequestParam(required = false) Long statusId) {
        return ResponseEntity.ok(invitationService.findByEventIdAndStatus(eventId, statusId));
    }

    @Operation(summary = "Obtener invitación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventInvitationResponse> findById(
            @Parameter(description = "ID de la invitación") @PathVariable Long id) {
        return ResponseEntity.ok(invitationService.findById(id));
    }

    @Operation(summary = "Crear nueva invitación",
               description = "Envía una invitación a un emprendimiento. Valida que el emprendimiento exista en entrepreneurship-service")
    @PostMapping
    public ResponseEntity<EventInvitationResponse> create(@Valid @RequestBody EventInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationService.create(request));
    }

    @Operation(summary = "Actualizar estado de invitación",
               description = "Actualiza el estado de una invitación (PENDING → ACCEPTED/REJECTED)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<EventInvitationResponse> updateStatus(
            @Parameter(description = "ID de la invitación") @PathVariable Long id,
            @Parameter(description = "Nuevo estado: 1=PENDING, 2=ACCEPTED, 3=REJECTED")
            @RequestParam Long statusId) {
        return ResponseEntity.ok(invitationService.updateStatus(id, statusId));
    }

    @Operation(summary = "Eliminar invitación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la invitación") @PathVariable Long id) {
        invitationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

