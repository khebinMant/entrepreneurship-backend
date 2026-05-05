package com.project.emprendia.shared.dto;

import com.project.emprendia.shared.enums.EntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating image gallery entries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageGalleryRequest {

    @NotNull(message = "Entity type is required")
    private EntityType entityType;

    @NotNull(message = "Entity ID is required")
    @Positive(message = "Entity ID must be positive")
    private Long entityId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotBlank(message = "File name is required")
    private String fileName;

    private Integer displayOrder;

    private String altText;

    private String description;

    private Integer fileSizeKb;

    private Integer widthPx;

    private Integer heightPx;

    private String mimeType;

    private Long uploadedByUserId;
}
