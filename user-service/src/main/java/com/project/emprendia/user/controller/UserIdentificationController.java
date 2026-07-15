package com.project.emprendia.user.controller;

import com.project.emprendia.user.dto.UserIdentificationRequest;
import com.project.emprendia.user.dto.UserIdentificationResponse;
import com.project.emprendia.user.service.UserIdentificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-identifications")
@RequiredArgsConstructor
public class UserIdentificationController {

    private final UserIdentificationService userIdentificationService;

    @GetMapping
    public ResponseEntity<List<UserIdentificationResponse>> findAll() {
        return ResponseEntity.ok(userIdentificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserIdentificationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userIdentificationService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserIdentificationResponse>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userIdentificationService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<UserIdentificationResponse> create(@Valid @RequestBody UserIdentificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userIdentificationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserIdentificationResponse> update(@PathVariable Long id,
                                                           @Valid @RequestBody UserIdentificationRequest request) {
        return ResponseEntity.ok(userIdentificationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userIdentificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
