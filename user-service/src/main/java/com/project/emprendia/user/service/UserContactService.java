package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.UserContactRequest;
import com.project.emprendia.user.dto.UserContactResponse;

import java.util.List;

public interface UserContactService {
    List<UserContactResponse> findAll();
    UserContactResponse findById(Long id);
    List<UserContactResponse> findByUserId(Long userId);
    UserContactResponse create(UserContactRequest request);
    UserContactResponse update(Long id, UserContactRequest request);
    void delete(Long id);
}
