package com.project.emprendia.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for catalogue values from shared-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogueValueResponse {
    private Long catalogueValueId;
    private String code;
    private String name;
    private String description;
    private Long parentValueId;
    private String parentValueName;
    private Long catalogueTypeId;
    private String catalogueTypeCode;
}

