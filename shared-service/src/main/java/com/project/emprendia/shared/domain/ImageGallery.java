package com.project.emprendia.shared.domain;

import com.project.emprendia.shared.enums.EntityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing images in galleries for different entities (users, entrepreneurships, events).
 * Stores metadata and URLs to images stored in Digital Ocean Spaces or local storage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "image_gallery",
    indexes = {
        @Index(name = "idx_image_gallery_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_image_gallery_order", columnList = "entity_type, entity_id, display_order")
    }
)
public class ImageGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "storage_path", columnDefinition = "TEXT")
    private String storagePath;  // Path relativo: users/1/file.jpg

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_size_kb")
    private Integer fileSizeKb;

    @Column(name = "width_px")
    private Integer widthPx;

    @Column(name = "height_px")
    private Integer heightPx;

    @Column(name = "mime_type", length = 50)
    private String mimeType;

    @Column(name = "uploaded_by_user_id")
    private Long uploadedByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
