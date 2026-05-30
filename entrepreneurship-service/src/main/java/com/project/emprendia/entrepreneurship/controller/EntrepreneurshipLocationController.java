package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationResponse;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipLocationService;
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
@RequestMapping("/api/v1/entrepreneurship-locations")
@RequiredArgsConstructor
@Tag(name = "Entrepreneurship Locations", description = "Gestión de ubicaciones físicas de emprendimientos")
public class EntrepreneurshipLocationController {

    private final EntrepreneurshipLocationService locationService;

    @Operation(summary = "Listar ubicaciones por emprendimiento",
               description = "Obtiene todas las ubicaciones físicas de un emprendimiento específico")
    @GetMapping("/entrepreneurship/{entrepreneurshipId}")
    public ResponseEntity<List<EntrepreneurshipLocationResponse>> findByEntrepreneurshipId(
            @Parameter(description = "ID del emprendimiento") @PathVariable Long entrepreneurshipId) {
        return ResponseEntity.ok(locationService.findByEntrepreneurshipId(entrepreneurshipId));
    }

    @Operation(summary = "Obtener ubicación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntrepreneurshipLocationResponse> findById(
            @Parameter(description = "ID de la ubicación") @PathVariable Long id) {
        return ResponseEntity.ok(locationService.findById(id));
    }

    @Operation(summary = "Crear nueva ubicación",
               description = "Crea una nueva ubicación física para un emprendimiento")
    @PostMapping
    public ResponseEntity<EntrepreneurshipLocationResponse> create(
            @Valid @RequestBody EntrepreneurshipLocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(request));
    }

    @Operation(summary = "Actualizar ubicación")
    @PutMapping("/{id}")
    public ResponseEntity<EntrepreneurshipLocationResponse> update(
            @Parameter(description = "ID de la ubicación") @PathVariable Long id,
            @Valid @RequestBody EntrepreneurshipLocationRequest request) {
        return ResponseEntity.ok(locationService.update(id, request));
    }

    @Operation(summary = "Eliminar ubicación")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la ubicación") @PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

