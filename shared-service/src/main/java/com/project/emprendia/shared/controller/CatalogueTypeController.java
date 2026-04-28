package com.project.emprendia.shared.controller;

import com.project.emprendia.shared.dto.CatalogueTypeRequest;
import com.project.emprendia.shared.dto.CatalogueTypeResponse;
import com.project.emprendia.shared.service.CatalogueTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogue-types")
@RequiredArgsConstructor
public class CatalogueTypeController {

    private final CatalogueTypeService catalogueTypeService;

    @GetMapping
    public ResponseEntity<List<CatalogueTypeResponse>> findAll() {
        return ResponseEntity.ok(catalogueTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogueTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogueTypeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CatalogueTypeResponse> create(@Valid @RequestBody CatalogueTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogueTypeResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody CatalogueTypeRequest request) {
        return ResponseEntity.ok(catalogueTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogueTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
