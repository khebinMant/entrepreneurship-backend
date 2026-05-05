package com.project.emprendia.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserIdentificationResponse {
    private Long userIdentificationId;
    private Long userId;
    private Long identificationTypeId;
    private String identificationNumber;
    private Long issuedCountryId;
}
