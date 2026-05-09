package com.project.emprendia.event.client;

import com.project.emprendia.event.dto.UserBasicResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for User Service
 */
@FeignClient(
    name = "user-service",
    url = "${services.user.url}",
    path = "/api/v1"
)
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    UserBasicResponse getUserById(@PathVariable Long id);

    default UserBasicResponse getUserByIdFallback(Long id, Throwable t) {
        return UserBasicResponse.builder()
            .userId(id)
            .firstName("N/A")
            .lastName("N/A")
            .build();
    }
}

