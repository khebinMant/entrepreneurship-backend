package com.project.emprendia.event.controller;

import com.project.emprendia.event.dto.BulkEventInvitationRequest;
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
               description = "Obtiene invitaciones de un evento. Filtra por statusId del catálogo INVITATION_STATUS (PENDING, ACCEPTED, REJECTED)")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventInvitationResponse>> findByEvent(
            @Parameter(description = "ID del evento") @PathVariable Long eventId,
            @Parameter(description = "statusId del catálogo INVITATION_STATUS (usa GET /api/v1/catalogue-values/by-type/INVITATION_STATUS)")
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

    @Operation(summary = "Crear múltiples invitaciones en lote",
               description = "Crea varias invitaciones en una sola petición. Cada invitación puede tener su propio mensaje. Los correos se envían automáticamente.")
    @PostMapping("/bulk")
    public ResponseEntity<List<EventInvitationResponse>> createBulk(@Valid @RequestBody BulkEventInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationService.createBulk(request));
    }

    @Operation(summary = "Actualizar estado de invitación",
               description = "Actualiza el estado de una invitación. El statusId debe obtenerse del catálogo INVITATION_STATUS (PENDING → ACCEPTED/REJECTED). Si se envía a PENDING, opcionalmente se puede incluir un message para el reenvío del correo.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<EventInvitationResponse> updateStatus(
            @Parameter(description = "ID de la invitación") @PathVariable Long id,
            @Parameter(description = "statusId del catálogo INVITATION_STATUS")
            @RequestParam Long statusId,
            @Parameter(description = "Mensaje personalizado para reenvío (solo aplica cuando statusId = PENDING)")
            @RequestParam(required = false) String message) {
        return ResponseEntity.ok(invitationService.updateStatus(id, statusId, message));
    }

    @Operation(summary = "Eliminar invitación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la invitación") @PathVariable Long id) {
        invitationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

