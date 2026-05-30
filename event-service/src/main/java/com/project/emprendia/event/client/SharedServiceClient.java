package com.project.emprendia.event.client;

import com.project.emprendia.event.dto.CatalogueValueResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Feign Client for Shared Service (Catalogues & Images)
 */
@FeignClient(
    name = "shared-service",
    url = "${services.shared.url}",
    configuration = com.project.emprendia.event.configuration.FeignClientConfiguration.class
)
public interface SharedServiceClient {

    @GetMapping("/api/v1/catalogue-values/by-type/{typeCode}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValuesByTypeFallback")
    List<CatalogueValueResponse> getValuesByType(@PathVariable String typeCode);

    @GetMapping("/api/v1/catalogue-values/{id}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValueByIdFallback")
    CatalogueValueResponse getValueById(@PathVariable Long id);

    @GetMapping("/api/images")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getImagesForEntityFallback")
    List<Map<String, Object>> getImagesForEntity(
        @RequestParam("entityType") String entityType,
        @RequestParam("entityId") Long entityId
    );

    default List<CatalogueValueResponse> getValuesByTypeFallback(String typeCode, Throwable t) {
        return Collections.emptyList();
    }

    default CatalogueValueResponse getValueByIdFallback(Long id, Throwable t) {
        return CatalogueValueResponse.builder()
            .catalogueValueId(id)
            .name("N/A")
            .code("N/A")
            .build();
    }

    default List<Map<String, Object>> getImagesForEntityFallback(String entityType, Long entityId, Throwable t) {
        return Collections.emptyList();
    }
}

