package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic entrepreneurship information DTO from entrepreneurship-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipBasicResponse {
    private Long entrepreneurshipId;
    private Long userId;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isPhysical;
    private Boolean isDigital;
    private String imageUrl;
    private Long imageId;
}

