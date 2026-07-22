package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

