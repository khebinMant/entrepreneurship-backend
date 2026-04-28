package com.project.emprendia.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserIdentificationRequest {

    @NotNull
    private Long identificationTypeId;

    @NotBlank
    @Size(max = 30)
    private String identificationNumber;

    private Long issuedCountryId;
}
