package com.project.emprendia.user.controller;

import com.project.emprendia.user.dto.ChangePasswordRequest;
import com.project.emprendia.user.dto.EmailUpdateRequest;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import com.project.emprendia.user.dto.UserUpdateRequest;
import com.project.emprendia.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> findByKeycloakId(@PathVariable String keycloakId) {
        return ResponseEntity.ok(userService.findByKeycloakId(keycloakId));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> getEmail(@PathVariable Long id) {
        String email = userService.getEmail(id);
        if (email == null) {
            return ResponseEntity.ok(Map.of("email", ""));
        }
        return ResponseEntity.ok(Map.of("email", email));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateEmail(@PathVariable Long id,
                                                     @Valid @RequestBody EmailUpdateRequest request) {
        return ResponseEntity.ok(userService.updateEmail(id, request.getEmail()));
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                                @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
