package com.project.emprendia.shared.controller;

import com.project.emprendia.shared.dto.CatalogueValueRequest;
import com.project.emprendia.shared.dto.CatalogueValueResponse;
import com.project.emprendia.shared.service.CatalogueValueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogue-values")
@RequiredArgsConstructor
public class CatalogueValueController {

    private final CatalogueValueService catalogueValueService;

    @GetMapping("/by-type/{typeCode}")
    public ResponseEntity<List<CatalogueValueResponse>> findByTypeCode(@PathVariable String typeCode) {
        return ResponseEntity.ok(catalogueValueService.findByTypeCode(typeCode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogueValueResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogueValueService.findById(id));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<CatalogueValueResponse>> findChildren(@PathVariable Long id) {
        return ResponseEntity.ok(catalogueValueService.findChildrenByParentId(id));
    }

    @PostMapping
    public ResponseEntity<CatalogueValueResponse> create(@Valid @RequestBody CatalogueValueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueValueService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogueValueResponse> update(@PathVariable Long id,
                                                          @Valid @RequestBody CatalogueValueRequest request) {
        return ResponseEntity.ok(catalogueValueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogueValueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
