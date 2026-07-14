package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entrepreneurships")
@RequiredArgsConstructor
public class EntrepreneurshipController {

    private final EntrepreneurshipService entrepreneurshipService;

    @GetMapping
    public ResponseEntity<List<EntrepreneurshipResponse>> findAll() {
        return ResponseEntity.ok(entrepreneurshipService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrepreneurshipResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(entrepreneurshipService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isPhysical,
            @RequestParam(required = false) Boolean isDigital,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<EntrepreneurshipResponse> result = entrepreneurshipService.findByUserIdPaginated(
                userId, name, categoryId, isPhysical, isDigital, pageable);
            return ResponseEntity.ok(result);
        }

        List<EntrepreneurshipResponse> result = entrepreneurshipService.findByUserId(
            userId, name, categoryId, isPhysical, isDigital);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isPhysical,
            @RequestParam(required = false) Boolean isDigital,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        // Si se proporcionan parámetros de paginación, retornar Page
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<EntrepreneurshipResponse> result = entrepreneurshipService.searchPaginated(
                name, categoryId, isPhysical, isDigital, pageable);
            return ResponseEntity.ok(result);
        }

        // Si no hay paginación, retornar lista completa (comportamiento existente)
        List<EntrepreneurshipResponse> result = entrepreneurshipService.search(
            name, categoryId, isPhysical, isDigital);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<EntrepreneurshipResponse> create(@Valid @RequestBody EntrepreneurshipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entrepreneurshipService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntrepreneurshipResponse> update(@PathVariable Long id,
                                                            @Valid @RequestBody EntrepreneurshipRequest request) {
        return ResponseEntity.ok(entrepreneurshipService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        entrepreneurshipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
