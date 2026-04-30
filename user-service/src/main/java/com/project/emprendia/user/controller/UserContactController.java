package com.project.emprendia.user.controller;

import com.project.emprendia.user.dto.UserContactRequest;
import com.project.emprendia.user.dto.UserContactResponse;
import com.project.emprendia.user.service.UserContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-contacts")
@RequiredArgsConstructor
public class UserContactController {

    private final UserContactService userContactService;

    @GetMapping
    public ResponseEntity<List<UserContactResponse>> findAll() {
        return ResponseEntity.ok(userContactService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserContactResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userContactService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserContactResponse> create(@Valid @RequestBody UserContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userContactService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserContactResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody UserContactRequest request) {
        return ResponseEntity.ok(userContactService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userContactService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
