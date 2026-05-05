package com.project.emprendia.shared.controller;

import com.project.emprendia.shared.dto.*;
import com.project.emprendia.shared.enums.EntityType;
import com.project.emprendia.shared.service.ImageGalleryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for image gallery operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageGalleryController {

    private final ImageGalleryService imageGalleryService;

    /**
     * Upload an image to the gallery
     * 
     * POST /api/images/upload
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam("entityId") Long entityId,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "uploadedByUserId", required = false) Long uploadedByUserId) {

        log.info("Upload request received for {} {}", entityType, entityId);

        ImageUploadRequest metadata = ImageUploadRequest.builder()
                .entityType(entityType)
                .entityId(entityId)
                .displayOrder(displayOrder)
                .altText(altText)
                .description(description)
                .uploadedByUserId(uploadedByUserId)
                .build();

        ImageUploadResponse response = imageGalleryService.uploadImage(file, metadata);

        return response.isSuccess() 
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Get all images for a specific entity
     * 
     * GET /api/images?entityType=USER&entityId=1
     */
    @GetMapping
    public ResponseEntity<List<ImageGalleryResponse>> getImagesForEntity(
            @RequestParam EntityType entityType,
            @RequestParam Long entityId) {

        log.info("Get images request for {} {}", entityType, entityId);
        List<ImageGalleryResponse> images = imageGalleryService.getImagesForEntity(entityType, entityId);
        return ResponseEntity.ok(images);
    }

    /**
     * Get a single image by ID
     * 
     * GET /api/images/{imageId}
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<ImageGalleryResponse> getImageById(@PathVariable Long imageId) {
        log.info("Get image by id: {}", imageId);
        ImageGalleryResponse image = imageGalleryService.getImageById(imageId);
        return ResponseEntity.ok(image);
    }

    /**
     * Update image metadata
     * 
     * PUT /api/images/{imageId}
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<ImageGalleryResponse> updateImageMetadata(
            @PathVariable Long imageId,
            @RequestBody ImageGalleryRequest request) {

        log.info("Update image metadata request for id: {}", imageId);
        ImageGalleryResponse updatedImage = imageGalleryService.updateImageMetadata(imageId, request);
        return ResponseEntity.ok(updatedImage);
    }

    /**
     * Delete an image
     * 
     * DELETE /api/images/{imageId}
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        log.info("Delete image request for id: {}", imageId);
        imageGalleryService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all images for a specific entity
     * 
     * DELETE /api/images?entityType=USER&entityId=1
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAllImagesForEntity(
            @RequestParam EntityType entityType,
            @RequestParam Long entityId) {

        log.info("Delete all images request for {} {}", entityType, entityId);
        int deletedCount = imageGalleryService.deleteAllImagesForEntity(entityType, entityId);
        return ResponseEntity.ok("Deleted " + deletedCount + " images");
    }

    /**
     * Reorder images in a gallery
     * 
     * POST /api/images/reorder?entityType=USER&entityId=1
     * Body: [1, 3, 2, 5, 4]
     */
    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderImages(
            @RequestParam EntityType entityType,
            @RequestParam Long entityId,
            @RequestBody List<Long> imageIds) {

        log.info("Reorder images request for {} {}", entityType, entityId);
        imageGalleryService.reorderImages(entityType, entityId, imageIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Count images for a specific entity
     * 
     * GET /api/images/count?entityType=USER&entityId=1
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countImagesForEntity(
            @RequestParam EntityType entityType,
            @RequestParam Long entityId) {

        log.info("Count images request for {} {}", entityType, entityId);
        long count = imageGalleryService.countImagesForEntity(entityType, entityId);
        return ResponseEntity.ok(count);
    }

    /**
     * Check if more images can be added
     * 
     * GET /api/images/can-add-more?entityType=USER&entityId=1
     */
    @GetMapping("/can-add-more")
    public ResponseEntity<Boolean> canAddMoreImages(
            @RequestParam EntityType entityType,
            @RequestParam Long entityId) {

        log.info("Can add more images check for {} {}", entityType, entityId);
        boolean canAdd = imageGalleryService.canAddMoreImages(entityType, entityId);
        return ResponseEntity.ok(canAdd);
    }
}
