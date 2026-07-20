package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Basic user information DTO from user-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicResponse {
    private Long userId;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String email;
    private List<UserContactResponse> contacts;
}

