package com.project.emprendia.shared.service;

import com.project.emprendia.shared.dto.*;
import com.project.emprendia.shared.enums.EntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing image galleries.
 */
public interface ImageGalleryService {

    /**
     * Upload an image to the gallery
     * 
     * @param file The multipart file to upload
     * @param metadata Upload metadata
     * @return Upload result with image URL
     */
    ImageUploadResponse uploadImage(MultipartFile file, ImageUploadRequest metadata);

    /**
     * Get all images for a specific entity
     * 
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @return List of images ordered by display order
     */
    List<ImageGalleryResponse> getImagesForEntity(EntityType entityType, Long entityId);

    /**
     * Get a single image by ID
     * 
     * @param imageId ID of the image
     * @return Image details
     */
    ImageGalleryResponse getImageById(Long imageId);

    /**
     * Update image metadata
     * 
     * @param imageId ID of the image
     * @param request Updated metadata
     * @return Updated image details
     */
    ImageGalleryResponse updateImageMetadata(Long imageId, ImageGalleryRequest request);

    /**
     * Delete an image
     * 
     * @param imageId ID of the image
     * @return true if deleted successfully
     */
    boolean deleteImage(Long imageId);

    /**
     * Delete all images for a specific entity
     * 
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @return Number of images deleted
     */
    int deleteAllImagesForEntity(EntityType entityType, Long entityId);

    /**
     * Reorder images in a gallery
     * 
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @param imageIds List of image IDs in the desired order
     */
    void reorderImages(EntityType entityType, Long entityId, List<Long> imageIds);

    /**
     * Count images for a specific entity
     * 
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @return Number of images
     */
    long countImagesForEntity(EntityType entityType, Long entityId);

    /**
     * Check if entity can add more images based on limits
     * 
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @return true if more images can be added
     */
    boolean canAddMoreImages(EntityType entityType, Long entityId);
}
