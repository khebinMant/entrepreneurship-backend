package com.project.emprendia.event.controller;

import com.project.emprendia.event.dto.EntityPortalRequest;
import com.project.emprendia.event.dto.EntityPortalResponse;
import com.project.emprendia.event.service.EntityPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/entity-portals")
@RequiredArgsConstructor
@Tag(name = "Entity Portals", description = "Gestión de portales web de eventos")
public class EntityPortalController {

    private final EntityPortalService portalService;

    @Operation(summary = "Obtener portal por evento",
               description = "Obtiene el portal web de un evento específico")
    @GetMapping("/by-entity/{entityId}")
    public ResponseEntity<EntityPortalResponse> findByEntityId(
            @Parameter(description = "ID del evento") @PathVariable Long entityId) {
        return ResponseEntity.ok(portalService.findByEntityId(entityId));
    }

    @Operation(summary = "Obtener portal por subdominio",
               description = "Obtiene el portal web de una entidad por su subdominio, usado para accesos con dominio dinámico")
    @GetMapping("/by-subdomain/{subdomain}")
    public ResponseEntity<EntityPortalResponse> findBySubdomain(
            @Parameter(description = "Subdominio del portal") @PathVariable String subdomain) {
        return ResponseEntity.ok(portalService.findBySubdomain(subdomain));
    }

    @Operation(summary = "Obtener portal por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityPortalResponse> findById(
            @Parameter(description = "ID del portal") @PathVariable Long id) {
        return ResponseEntity.ok(portalService.findById(id));
    }

    @Operation(summary = "Crear nuevo portal",
               description = "Crea un portal web personalizado para un evento con subdomain único")
    @PostMapping
    public ResponseEntity<EntityPortalResponse> create(
            @Valid @RequestBody EntityPortalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portalService.create(request));
    }

    @Operation(summary = "Actualizar portal")
    @PutMapping("/{id}")
    public ResponseEntity<EntityPortalResponse> update(
            @Parameter(description = "ID del portal") @PathVariable Long id,
            @Valid @RequestBody EntityPortalRequest request) {
        return ResponseEntity.ok(portalService.update(id, request));
    }

    @Operation(summary = "Eliminar portal")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del portal") @PathVariable Long id) {
        portalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
