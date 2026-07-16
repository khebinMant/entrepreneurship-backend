package com.project.emprendia.event.service;

import com.project.emprendia.event.client.SharedServiceClient;
import com.project.emprendia.event.dto.CatalogueValueResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final SharedServiceClient sharedServiceClient;

    private final ConcurrentMap<String, List<CatalogueValueResponse>> cache = new ConcurrentHashMap<>();

    public Long getStatusId(String typeCode, String statusCode) {
        List<CatalogueValueResponse> values = cache.computeIfAbsent(typeCode, tc -> {
            try {
                return sharedServiceClient.getValuesByType(tc);
            } catch (Exception e) {
                log.error("Error fetching catalogue values for type {}: {}", tc, e.getMessage());
                return List.of();
            }
        });

        return values.stream()
            .filter(v -> statusCode.equals(v.getCode()))
            .map(CatalogueValueResponse::getCatalogueValueId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                "Status '" + statusCode + "' not found in catalogue type '" + typeCode + "'"));
    }
}
