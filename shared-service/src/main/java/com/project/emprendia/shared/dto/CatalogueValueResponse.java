package com.project.emprendia.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
