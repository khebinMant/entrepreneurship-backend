package com.project.emprendia.user.client;

import com.project.emprendia.user.dto.CatalogueValueResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

/**
 * Feign Client for Shared Service (Catalogues)
 */
@FeignClient(
    name = "shared-service",
    url = "${services.shared.url}",
    path = "/api/v1"
)
public interface SharedServiceClient {

    @GetMapping("/catalogue-values/by-type/{typeCode}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValuesByTypeFallback")
    List<CatalogueValueResponse> getValuesByType(@PathVariable String typeCode);

    @GetMapping("/catalogue-values/{id}")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getValueByIdFallback")
    CatalogueValueResponse getValueById(@PathVariable Long id);

    @GetMapping("/catalogue-values/{id}/children")
    @CircuitBreaker(name = "sharedService", fallbackMethod = "getChildrenFallback")
    List<CatalogueValueResponse> getChildren(@PathVariable Long id);

    // Fallback methods
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

    default List<CatalogueValueResponse> getChildrenFallback(Long id, Throwable t) {
        return Collections.emptyList();
    }
}

