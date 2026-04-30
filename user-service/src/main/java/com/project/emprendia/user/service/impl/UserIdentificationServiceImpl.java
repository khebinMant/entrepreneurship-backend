package com.project.emprendia.user.service.impl;

import com.project.emprendia.user.domain.UserIdentification;
import com.project.emprendia.user.dto.UserIdentificationRequest;
import com.project.emprendia.user.dto.UserIdentificationResponse;
import com.project.emprendia.user.repository.UserIdentificationRepository;
import com.project.emprendia.user.service.UserIdentificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserIdentificationServiceImpl implements UserIdentificationService {

    private final UserIdentificationRepository userIdentificationRepository;

    @Override
    public List<UserIdentificationResponse> findAll() {
        return userIdentificationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserIdentificationResponse findById(Long id) {
        return userIdentificationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User identification not found"));
    }

    @Override
    @Transactional
    public UserIdentificationResponse create(UserIdentificationRequest request) {
        UserIdentification identification = toEntity(request);
        return toResponse(userIdentificationRepository.save(identification));
    }

    @Override
    @Transactional
    public UserIdentificationResponse update(Long id, UserIdentificationRequest request) {
        UserIdentification identification = userIdentificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User identification not found"));
        updateEntity(identification, request);
        return toResponse(userIdentificationRepository.save(identification));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userIdentificationRepository.deleteById(id);
    }

    private UserIdentificationResponse toResponse(UserIdentification identification) {
        UserIdentificationResponse response = new UserIdentificationResponse();
        response.setId(identification.getId());
        response.setIdentificationTypeId(identification.getIdentificationTypeId());
        response.setIdentificationNumber(identification.getIdentificationNumber());
        response.setIssuedCountryId(identification.getIssuedCountryId());
        return response;
    }

    private UserIdentification toEntity(UserIdentificationRequest request) {
        UserIdentification identification = new UserIdentification();
        identification.setIdentificationTypeId(request.getIdentificationTypeId());
        identification.setIdentificationNumber(request.getIdentificationNumber());
        identification.setIssuedCountryId(request.getIssuedCountryId());
        return identification;
    }

    private void updateEntity(UserIdentification identification, UserIdentificationRequest request) {
        identification.setIdentificationTypeId(request.getIdentificationTypeId());
        identification.setIdentificationNumber(request.getIdentificationNumber());
        identification.setIssuedCountryId(request.getIssuedCountryId());
    }
}
