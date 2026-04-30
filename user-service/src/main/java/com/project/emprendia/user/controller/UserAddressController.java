package com.project.emprendia.user.controller;

import com.project.emprendia.user.dto.UserAddressRequest;
import com.project.emprendia.user.dto.UserAddressResponse;
import com.project.emprendia.user.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> findAll() {
        return ResponseEntity.ok(userAddressService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAddressResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userAddressService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserAddressResponse> create(@Valid @RequestBody UserAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAddressService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAddressResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody UserAddressRequest request) {
        return ResponseEntity.ok(userAddressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userAddressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
