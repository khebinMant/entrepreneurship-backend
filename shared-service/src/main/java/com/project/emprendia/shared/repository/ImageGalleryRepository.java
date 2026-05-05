package com.project.emprendia.shared.repository;

import com.project.emprendia.shared.domain.ImageGallery;
import com.project.emprendia.shared.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ImageGallery entity operations.
 */
@Repository
public interface ImageGalleryRepository extends JpaRepository<ImageGallery, Long> {

    /**
     * Find all images for a specific entity, ordered by display order
     */
    List<ImageGallery> findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(
            EntityType entityType, 
            Long entityId
    );

    /**
     * Find all images for a specific entity
     */
    List<ImageGallery> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    /**
     * Count images for a specific entity
     */
    long countByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    /**
     * Find image by entity type, entity id and display order
     */
    Optional<ImageGallery> findByEntityTypeAndEntityIdAndDisplayOrder(
            EntityType entityType, 
            Long entityId, 
            Integer displayOrder
    );

    /**
     * Delete all images for a specific entity
     */
    void deleteByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    /**
     * Find all images uploaded by a specific user
     */
    List<ImageGallery> findByUploadedByUserId(Long uploadedByUserId);

    /**
     * Get the maximum display order for a specific entity
     */
    @Query("SELECT COALESCE(MAX(ig.displayOrder), 0) FROM ImageGallery ig " +
           "WHERE ig.entityType = :entityType AND ig.entityId = :entityId")
    Integer getMaxDisplayOrder(
            @Param("entityType") EntityType entityType, 
            @Param("entityId") Long entityId
    );
}
