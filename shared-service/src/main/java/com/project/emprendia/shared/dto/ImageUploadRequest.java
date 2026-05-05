package com.project.emprendia.shared.dto;

import com.project.emprendia.shared.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for image upload metadata.
 * Used when uploading files through multipart/form-data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {

    private EntityType entityType;
    private Long entityId;
    private Integer displayOrder;
    private String altText;
    private String description;
    private Long uploadedByUserId;
}
