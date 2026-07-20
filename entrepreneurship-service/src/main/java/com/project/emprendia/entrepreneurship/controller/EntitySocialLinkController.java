package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkRequest;
import com.project.emprendia.entrepreneurship.dto.EntitySocialLinkResponse;
import com.project.emprendia.entrepreneurship.service.EntitySocialLinkService;
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
@RequestMapping("/api/v1/entity-social-links")
@RequiredArgsConstructor
@Tag(name = "Entity Social Links", description = "Gestión de redes sociales de entidades (eventos/emprendimientos)")
public class EntitySocialLinkController {

    private final EntitySocialLinkService socialLinkService;

    @Operation(summary = "Listar redes sociales por entidad",
               description = "Obtiene todas las redes sociales de una entidad (evento o emprendimiento)")
    @GetMapping("/by-entity/{entityId}")
    public ResponseEntity<List<EntitySocialLinkResponse>> findByEntityId(
            @Parameter(description = "ID de la entidad") @PathVariable Long entityId) {
        return ResponseEntity.ok(socialLinkService.findByEntityId(entityId));
    }

    @Operation(summary = "Obtener red social por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntitySocialLinkResponse> findById(
            @Parameter(description = "ID de la red social") @PathVariable Long id) {
        return ResponseEntity.ok(socialLinkService.findById(id));
    }

    @Operation(summary = "Crear nueva red social",
               description = "Agrega una nueva red social a una entidad (evento o emprendimiento)")
    @PostMapping
    public ResponseEntity<EntitySocialLinkResponse> create(
            @Valid @RequestBody EntitySocialLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socialLinkService.create(request));
    }

    @Operation(summary = "Actualizar red social")
    @PutMapping("/{id}")
    public ResponseEntity<EntitySocialLinkResponse> update(
            @Parameter(description = "ID de la red social") @PathVariable Long id,
            @Valid @RequestBody EntitySocialLinkRequest request) {
        return ResponseEntity.ok(socialLinkService.update(id, request));
    }

    @Operation(summary = "Eliminar red social")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la red social") @PathVariable Long id) {
        socialLinkService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
