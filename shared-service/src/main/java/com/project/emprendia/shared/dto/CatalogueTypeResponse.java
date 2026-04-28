package com.project.emprendia.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogueTypeResponse {
    private Long catalogueTypeId;
    private String code;
    private String name;
    private String description;
}
