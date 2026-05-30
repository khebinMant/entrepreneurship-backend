package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipLocationResponse {
    private Long locationId;
    private Long entrepreneurshipId;
    private String entrepreneurshipName;
    private Long countryId;
    private Long provinceId;
    private Long cityId;
    private Long parishId;
    private String addressLine;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
}

