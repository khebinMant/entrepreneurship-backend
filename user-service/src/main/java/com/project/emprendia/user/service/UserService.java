package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse findByKeycloakId(String keycloakId);

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);
}
