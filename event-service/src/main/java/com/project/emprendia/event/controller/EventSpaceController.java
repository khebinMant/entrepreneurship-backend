package com.project.emprendia.event.controller;

import com.project.emprendia.event.dto.EventSpaceRequest;
import com.project.emprendia.event.dto.EventSpaceResponse;
import com.project.emprendia.event.service.EventSpaceService;
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
@RequestMapping("/api/v1/event-spaces")
@RequiredArgsConstructor
@Tag(name = "Event Spaces", description = "Gestión de espacios/stands disponibles en eventos")
public class EventSpaceController {

    private final EventSpaceService spaceService;

    @Operation(summary = "Listar espacios por evento",
               description = "Obtiene todos los espacios/stands de un evento específico")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventSpaceResponse>> findByEventId(
            @Parameter(description = "ID del evento") @PathVariable Long eventId) {
        return ResponseEntity.ok(spaceService.findByEventId(eventId));
    }

    @Operation(summary = "Obtener espacio por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventSpaceResponse> findById(
            @Parameter(description = "ID del espacio") @PathVariable Long id) {
        return ResponseEntity.ok(spaceService.findById(id));
    }

    @Operation(summary = "Crear nuevo espacio",
               description = "Crea un nuevo espacio/stand para un evento")
    @PostMapping
    public ResponseEntity<EventSpaceResponse> create(@Valid @RequestBody EventSpaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spaceService.create(request));
    }

    @Operation(summary = "Actualizar espacio")
    @PutMapping("/{id}")
    public ResponseEntity<EventSpaceResponse> update(
            @Parameter(description = "ID del espacio") @PathVariable Long id,
            @Valid @RequestBody EventSpaceRequest request) {
        return ResponseEntity.ok(spaceService.update(id, request));
    }

    @Operation(summary = "Eliminar espacio")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del espacio") @PathVariable Long id) {
        spaceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

