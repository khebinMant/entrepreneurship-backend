package com.project.emprendia.shared.dto;

import com.project.emprendia.shared.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for image gallery responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageGalleryResponse {

    private Long imageId;
    private EntityType entityType;
    private Long entityId;
    private String imageUrl;            // URL completa: http://localhost:8084/api/files/users/1/file.jpg
    private String storagePath;         // Path relativo: users/1/file.jpg (útil para desarrollo local)
    private String fileName;
    private Integer displayOrder;
    private String altText;
    private String description;
    private Integer fileSizeKb;
    private Integer widthPx;
    private Integer heightPx;
    private String mimeType;
    private Long uploadedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
