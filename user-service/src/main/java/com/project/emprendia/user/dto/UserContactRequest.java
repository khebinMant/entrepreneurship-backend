package com.project.emprendia.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserContactRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull
    private Long contactTypeId;

    @NotBlank
    @Size(max = 150)
    private String contactValue;

    private Boolean isPrimary = false;
}
