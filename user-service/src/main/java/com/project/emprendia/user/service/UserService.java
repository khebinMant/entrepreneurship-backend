package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.ChangePasswordRequest;
import com.project.emprendia.user.dto.UserRequest;
import com.project.emprendia.user.dto.UserResponse;
import com.project.emprendia.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse findByKeycloakId(String keycloakId);

    UserResponse create(UserRequest request);

    UserResponse update(Long id, UserUpdateRequest request);

    String getEmail(Long id);

    void changePassword(Long id, ChangePasswordRequest request);

    void delete(Long id);
}
