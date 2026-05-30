package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipSocialLinkRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipSocialLinkResponse;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipSocialLinkService;
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
@RequestMapping("/api/v1/entrepreneurship-social-links")
@RequiredArgsConstructor
@Tag(name = "Entrepreneurship Social Links", description = "Gestión de redes sociales de emprendimientos")
public class EntrepreneurshipSocialLinkController {

    private final EntrepreneurshipSocialLinkService socialLinkService;

    @Operation(summary = "Listar redes sociales por emprendimiento",
               description = "Obtiene todas las redes sociales de un emprendimiento específico")
    @GetMapping("/entrepreneurship/{entrepreneurshipId}")
    public ResponseEntity<List<EntrepreneurshipSocialLinkResponse>> findByEntrepreneurshipId(
            @Parameter(description = "ID del emprendimiento") @PathVariable Long entrepreneurshipId) {
        return ResponseEntity.ok(socialLinkService.findByEntrepreneurshipId(entrepreneurshipId));
    }

    @Operation(summary = "Obtener red social por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntrepreneurshipSocialLinkResponse> findById(
            @Parameter(description = "ID de la red social") @PathVariable Long id) {
        return ResponseEntity.ok(socialLinkService.findById(id));
    }

    @Operation(summary = "Crear nueva red social",
               description = "Agrega una nueva red social a un emprendimiento")
    @PostMapping
    public ResponseEntity<EntrepreneurshipSocialLinkResponse> create(
            @Valid @RequestBody EntrepreneurshipSocialLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socialLinkService.create(request));
    }

    @Operation(summary = "Actualizar red social")
    @PutMapping("/{id}")
    public ResponseEntity<EntrepreneurshipSocialLinkResponse> update(
            @Parameter(description = "ID de la red social") @PathVariable Long id,
            @Valid @RequestBody EntrepreneurshipSocialLinkRequest request) {
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

