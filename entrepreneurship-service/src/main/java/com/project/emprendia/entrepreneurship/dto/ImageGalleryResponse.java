package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de imagen desde shared-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageGalleryResponse {
    private Long imageId;
    private String entityType;
    private Long entityId;
    private String imageUrl;
    private String fileName;
    private Integer displayOrder;
    private String altText;
    private String description;
}

