package com.project.emprendia.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAddressResponse {
    private Long userAddressId;
    private Long userId;
    private Long countryId;
    private Long provinceId;
    private Long cityId;
    private Long parishId;
    private String addressLine;
    private String reference;
    private Boolean isPrimary;
}
