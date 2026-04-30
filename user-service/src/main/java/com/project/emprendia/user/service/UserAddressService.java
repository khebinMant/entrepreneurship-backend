package com.project.emprendia.user.service;

import com.project.emprendia.user.dto.UserAddressRequest;
import com.project.emprendia.user.dto.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    List<UserAddressResponse> findAll();
    UserAddressResponse findById(Long id);
    UserAddressResponse create(UserAddressRequest request);
    UserAddressResponse update(Long id, UserAddressRequest request);
    void delete(Long id);
}
