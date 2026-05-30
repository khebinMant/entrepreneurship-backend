package com.project.emprendia.entrepreneurship.service.impl;

import com.project.emprendia.entrepreneurship.client.SharedServiceClient;
import com.project.emprendia.entrepreneurship.domain.Entrepreneurship;
import com.project.emprendia.entrepreneurship.domain.EntrepreneurshipLocation;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationRequest;
import com.project.emprendia.entrepreneurship.dto.EntrepreneurshipLocationResponse;
import com.project.emprendia.entrepreneurship.exception.ResourceNotFoundException;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipLocationRepository;
import com.project.emprendia.entrepreneurship.repository.EntrepreneurshipRepository;
import com.project.emprendia.entrepreneurship.service.EntrepreneurshipLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntrepreneurshipLocationServiceImpl implements EntrepreneurshipLocationService {

    private final EntrepreneurshipLocationRepository locationRepository;
    private final EntrepreneurshipRepository entrepreneurshipRepository;

    @Override
    public List<EntrepreneurshipLocationResponse> findByEntrepreneurshipId(Long entrepreneurshipId) {
        return locationRepository.findByEntrepreneurshipEntrepreneurshipId(entrepreneurshipId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public EntrepreneurshipLocationResponse findById(Long id) {
        return toResponse(locationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntrepreneurshipLocation", id)));
    }

    @Override
    @Transactional
    public EntrepreneurshipLocationResponse create(EntrepreneurshipLocationRequest request) {
        Entrepreneurship entrepreneurship = entrepreneurshipRepository
            .findById(request.getEntrepreneurshipId())
            .orElseThrow(() -> new ResourceNotFoundException("Entrepreneurship", request.getEntrepreneurshipId()));

        EntrepreneurshipLocation location = EntrepreneurshipLocation.builder()
            .entrepreneurship(entrepreneurship)
            .countryId(request.getCountryId())
            .provinceId(request.getProvinceId())
            .cityId(request.getCityId())
            .parishId(request.getParishId())
            .addressLine(request.getAddressLine())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();

        return toResponse(locationRepository.save(location));
    }

    @Override
    @Transactional
    public EntrepreneurshipLocationResponse update(Long id, EntrepreneurshipLocationRequest request) {
        EntrepreneurshipLocation location = locationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("EntrepreneurshipLocation", id));

        location.setCountryId(request.getCountryId());
        location.setProvinceId(request.getProvinceId());
        location.setCityId(request.getCityId());
        location.setParishId(request.getParishId());
        location.setAddressLine(request.getAddressLine());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        return toResponse(locationRepository.save(location));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("EntrepreneurshipLocation", id);
        }
        locationRepository.deleteById(id);
    }

    private EntrepreneurshipLocationResponse toResponse(EntrepreneurshipLocation location) {
        return EntrepreneurshipLocationResponse.builder()
            .locationId(location.getLocationId())
            .entrepreneurshipId(location.getEntrepreneurship().getEntrepreneurshipId())
            .entrepreneurshipName(location.getEntrepreneurship().getName())
            .countryId(location.getCountryId())
            .provinceId(location.getProvinceId())
            .cityId(location.getCityId())
            .parishId(location.getParishId())
            .addressLine(location.getAddressLine())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .createdAt(location.getCreatedAt())
            .build();
    }
}

