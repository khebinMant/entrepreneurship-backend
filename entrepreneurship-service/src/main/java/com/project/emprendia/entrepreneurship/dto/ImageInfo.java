package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para información de imagen del emprendimiento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfo {
    private Long imageId;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
}

