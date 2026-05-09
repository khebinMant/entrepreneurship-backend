package com.project.emprendia.event.client;

import com.project.emprendia.event.dto.EntrepreneurshipBasicResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Entrepreneurship Service
 */
@FeignClient(
    name = "entrepreneurship-service",
    url = "${services.entrepreneurship.url}",
    path = "/api/v1"
)
public interface EntrepreneurshipServiceClient {

    @GetMapping("/entrepreneurships/{id}")
    @CircuitBreaker(name = "entrepreneurshipService", fallbackMethod = "getEntrepreneurshipByIdFallback")
    EntrepreneurshipBasicResponse getEntrepreneurshipById(@PathVariable Long id);

    default EntrepreneurshipBasicResponse getEntrepreneurshipByIdFallback(Long id, Throwable t) {
        return EntrepreneurshipBasicResponse.builder()
            .entrepreneurshipId(id)
            .name("N/A")
            .build();
    }
}

