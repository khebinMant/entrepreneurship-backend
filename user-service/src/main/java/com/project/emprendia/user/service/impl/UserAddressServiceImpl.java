package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.AppUser;
import com.project.emprendia.user.domain.UserAddress;
import com.project.emprendia.user.dto.UserAddressRequest;
import com.project.emprendia.user.dto.UserAddressResponse;
import com.project.emprendia.user.repository.UserAddressRepository;
import com.project.emprendia.user.repository.UserRepository;
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
    private final UserRepository userRepository;

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
    public List<UserAddressResponse> findByUserId(Long userId) {
        return userAddressRepository.findByUser_UserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserAddressResponse create(UserAddressRequest request) {
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            userAddressRepository.findByUser_UserId(request.getUserId())
                    .forEach(a -> a.setIsPrimary(false));
        }
        UserAddress address = toEntity(request);
        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public UserAddressResponse update(Long id, UserAddressRequest request) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User address not found"));
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            userAddressRepository.findByUser_UserId(address.getUser().getUserId())
                    .forEach(a -> a.setIsPrimary(false));
        }
        updateEntity(address, request);
        return toResponse(userAddressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userAddressRepository.deleteById(id);
    }

    private UserAddressResponse toResponse(UserAddress address) {
        return UserAddressResponse.builder()
                .userAddressId(address.getUserAddressId())
                .userId(address.getUser().getUserId())
                .countryId(address.getCountryId())
                .provinceId(address.getProvinceId())
                .cityId(address.getCityId())
                .parishId(address.getParishId())
                .addressLine(address.getAddressLine())
                .reference(address.getReference())
                .isPrimary(address.getIsPrimary())
                .build();
    }

    private UserAddress toEntity(UserAddressRequest request) {
        AppUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        
        UserAddress address = new UserAddress();
        address.setUser(user);
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
