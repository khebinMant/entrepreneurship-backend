package com.project.emprendia.entrepreneurship.controller;

import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipResponse;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<EntrepreneurshipResponse>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(entrepreneurshipService.findByUserId(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EntrepreneurshipResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isPhysical,
            @RequestParam(required = false) Boolean isDigital) {
        return ResponseEntity.ok(entrepreneurshipService.search(name, categoryId, isPhysical, isDigital));
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
