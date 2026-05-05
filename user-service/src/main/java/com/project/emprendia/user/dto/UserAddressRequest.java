package com.project.emprendia.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAddressRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull
    private Long countryId;

    private Long provinceId;
    private Long cityId;
    private Long parishId;
    private String addressLine;
    private String reference;
    private Boolean isPrimary = false;
}
