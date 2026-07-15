package com.project.emprendia.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private String profilePictureUrl;

    @Email(message = "Email must be valid")
    @Size(max = 255)
    private String email;
}
