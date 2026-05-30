package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipPortalRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipPortalResponse;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/entrepreneurship-portals")
@RequiredArgsConstructor
@Tag(name = "Entrepreneurship Portals", description = "Gestión de portales web personalizados de emprendimientos")
public class EntrepreneurshipPortalController {

    private final EntrepreneurshipPortalService portalService;

    @Operation(summary = "Obtener portal por emprendimiento",
               description = "Obtiene el portal web de un emprendimiento específico")
    @GetMapping("/entrepreneurship/{entrepreneurshipId}")
    public ResponseEntity<EntrepreneurshipPortalResponse> findByEntrepreneurshipId(
            @Parameter(description = "ID del emprendimiento") @PathVariable Long entrepreneurshipId) {
        return ResponseEntity.ok(portalService.findByEntrepreneurshipId(entrepreneurshipId));
    }

    @Operation(summary = "Obtener portal por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntrepreneurshipPortalResponse> findById(
            @Parameter(description = "ID del portal") @PathVariable Long id) {
        return ResponseEntity.ok(portalService.findById(id));
    }

    @Operation(summary = "Crear nuevo portal",
               description = "Crea un portal web personalizado para un emprendimiento con subdomain único")
    @PostMapping
    public ResponseEntity<EntrepreneurshipPortalResponse> create(
            @Valid @RequestBody EntrepreneurshipPortalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portalService.create(request));
    }

    @Operation(summary = "Actualizar portal")
    @PutMapping("/{id}")
    public ResponseEntity<EntrepreneurshipPortalResponse> update(
            @Parameter(description = "ID del portal") @PathVariable Long id,
            @Valid @RequestBody EntrepreneurshipPortalRequest request) {
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

