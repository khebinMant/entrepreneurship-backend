package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.UserIdentificationRequest;
import com.project.emprendia.user.dto.UserIdentificationResponse;

import java.util.List;

public interface UserIdentificationService {
    List<UserIdentificationResponse> findAll();
    UserIdentificationResponse findById(Long id);
    UserIdentificationResponse create(UserIdentificationRequest request);
    UserIdentificationResponse update(Long id, UserIdentificationRequest request);
    void delete(Long id);
}
