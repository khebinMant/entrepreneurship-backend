package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.UserAddress;
import com.project.emprendia.user.dto.UserAddressRequest;
import com.project.emprendia.user.dto.UserAddressResponse;
import com.project.emprendia.user.repository.UserAddressRepository;
import com.project.emprendia.user.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;

    @Override
    public List<UserAddressResponse> findAll() {
        return userAddressRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserAddressResponse findById(Long id) {
        return userAddressRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User address not found"));
    }

    @Override
    @Transactional
    public UserAddressResponse create(UserAddressRequest request) {
        UserAddress address = toEntity(request);
        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public UserAddressResponse update(Long id, UserAddressRequest request) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User address not found"));
        updateEntity(address, request);
        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userAddressRepository.deleteById(id);
    }

    private UserAddressResponse toResponse(UserAddress address) {
        UserAddressResponse response = new UserAddressResponse();
        response.setId(address.getId());
        response.setCountryId(address.getCountryId());
        response.setProvinceId(address.getProvinceId());
        response.setCityId(address.getCityId());
        response.setParishId(address.getParishId());
        response.setAddressLine(address.getAddressLine());
        response.setReference(address.getReference());
        response.setIsPrimary(address.getIsPrimary());
        return response;
    }

    private UserAddress toEntity(UserAddressRequest request) {
        UserAddress address = new UserAddress();
        address.setCountryId(request.getCountryId());
        address.setProvinceId(request.getProvinceId());
        address.setCityId(request.getCityId());
        address.setParishId(request.getParishId());
        address.setAddressLine(request.getAddressLine());
        address.setReference(request.getReference());
        address.setIsPrimary(request.getIsPrimary());
        return address;
    }

    private void updateEntity(UserAddress address, UserAddressRequest request) {
        address.setCountryId(request.getCountryId());
        address.setProvinceId(request.getProvinceId());
        address.setCityId(request.getCityId());
        address.setParishId(request.getParishId());
        address.setAddressLine(request.getAddressLine());
        address.setReference(request.getReference());
        address.setIsPrimary(request.getIsPrimary());
    }
}
