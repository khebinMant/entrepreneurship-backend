package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntityPortalRequest;
import com.project.emprendia.entrepreneurship.dto.EntityPortalResponse;
import com.project.emprendia.entrepreneurship.service.EntityPortalService;
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
@Tag(name = "Entity Portals", description = "Gestión de portales web de entidades (eventos/emprendimientos)")
public class EntityPortalController {

    private final EntityPortalService portalService;

    @Operation(summary = "Obtener portal por entidad",
               description = "Obtiene el portal web de una entidad específica (evento o emprendimiento)")
    @GetMapping("/by-entity/{entityId}")
    public ResponseEntity<EntityPortalResponse> findByEntityId(
            @Parameter(description = "ID de la entidad") @PathVariable Long entityId) {
        return ResponseEntity.ok(portalService.findByEntityId(entityId));
    }

    @Operation(summary = "Obtener portal por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityPortalResponse> findById(
            @Parameter(description = "ID del portal") @PathVariable Long id) {
        return ResponseEntity.ok(portalService.findById(id));
    }

    @Operation(summary = "Crear nuevo portal",
               description = "Crea un portal web personalizado para una entidad con subdomain único")
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
