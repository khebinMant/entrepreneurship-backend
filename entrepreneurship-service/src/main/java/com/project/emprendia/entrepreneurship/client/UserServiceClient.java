package com.project.emprendia.entrepreneurship.client;

import com.project.emprendia.entrepreneurship.dto.UserBasicResponse;
import com.project.emprendia.entrepreneurship.dto.UserContactResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

@FeignClient(
    name = "user-service",
    url = "${services.user.url}",
    path = "/api/v1"
)
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    UserBasicResponse getUserById(@PathVariable Long id);

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

    default List<UserContactResponse> getUserContactsFallback(Long userId, Throwable t) {
        return Collections.emptyList();
    }
}

