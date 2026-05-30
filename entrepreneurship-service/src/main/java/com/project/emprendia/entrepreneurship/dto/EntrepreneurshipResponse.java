package com.project.emprendia.entrepreneurship.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EntrepreneurshipResponse {
    private Long entrepreneurshipId;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean isPhysical;
    private Boolean isDigital;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Imagen del logo/principal del emprendimiento
    private String imageUrl;
    private Long imageId;
}
