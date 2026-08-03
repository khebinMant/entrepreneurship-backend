package com.project.emprendia.event.client;

import com.project.emprendia.event.dto.UserBasicResponse;
import com.project.emprendia.event.dto.UserContactResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Feign Client for User Service
 */
@FeignClient(
    name = "user-service",
    url = "${SERVICES_USER_URL:http://localhost:8081}",
    path = "/api/v1",
    configuration = com.project.emprendia.event.configuration.FeignClientConfiguration.class
)
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    UserBasicResponse getUserById(@PathVariable Long id);

    @GetMapping("/users/{id}/email")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserEmailFallback")
    Map<String, String> getUserEmail(@PathVariable Long id);

    @GetMapping("/user-contacts/user/{userId}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserContactsFallback")
    List<UserContactResponse> getUserContacts(@PathVariable Long userId);

    default UserBasicResponse getUserByIdFallback(Long id, Throwable t) {
        return UserBasicResponse.builder()
            .userId(id)
            .firstName("N/A")
            .lastName("N/A")
            .build();
    }

    default Map<String, String> getUserEmailFallback(Long id, Throwable t) {
        return Map.of("email", "");
    }

    default List<UserContactResponse> getUserContactsFallback(Long userId, Throwable t) {
        return Collections.emptyList();
    }
}

