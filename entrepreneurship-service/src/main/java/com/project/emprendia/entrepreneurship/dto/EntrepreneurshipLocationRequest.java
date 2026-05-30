package com.project.emprendia.entrepreneurship.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipLocationRequest {
    
    @NotNull(message = "Entrepreneurship ID is required")
    private Long entrepreneurshipId;
    
    @NotNull(message = "Country ID is required")
    private Long countryId;
    
    @NotNull(message = "Province ID is required")
    private Long provinceId;
    
    @NotNull(message = "City ID is required")
    private Long cityId;
    
    private Long parishId;
    
    private String addressLine;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
}

