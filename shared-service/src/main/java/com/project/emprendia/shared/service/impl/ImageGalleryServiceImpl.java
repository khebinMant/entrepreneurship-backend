package com.project.emprendia.shared.service.impl;

import com.project.emprendia.shared.domain.ImageGallery;
import com.project.emprendia.shared.dto.*;
import com.project.emprendia.shared.enums.EntityType;
import com.project.emprendia.shared.exception.StorageException;
import com.project.emprendia.shared.mapping.ImageGalleryMapper;
import com.project.emprendia.shared.repository.ImageGalleryRepository;
import com.project.emprendia.shared.service.ImageGalleryService;
import com.project.emprendia.shared.storage.StorageFile;
import com.project.emprendia.shared.storage.StorageResult;
import com.project.emprendia.shared.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ImageGalleryService.
 * Handles image upload, storage, and gallery management with configurable limits.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGalleryServiceImpl implements ImageGalleryService {

    private final ImageGalleryRepository imageGalleryRepository;
    private final ImageGalleryMapper imageGalleryMapper;
    private final StorageService storageService;

    // Image limits by entity type
    private static final Map<EntityType, Integer> IMAGE_LIMITS = Map.of(
            EntityType.USER, 5,
            EntityType.ENTREPRENEURSHIP, 10,
            EntityType.EVENT, 20
    );

    // Allowed image types
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    // Max file size: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    @Transactional
    public ImageUploadResponse uploadImage(MultipartFile file, ImageUploadRequest metadata) {
        try {
            // Validate file
            validateFile(file);

            // Check image limit
            if (!canAddMoreImages(metadata.getEntityType(), metadata.getEntityId())) {
                return ImageUploadResponse.builder()
                        .success(false)
                        .message("Image limit reached for this entity")
                        .build();
            }

            // Read image dimensions
            byte[] fileBytes = file.getBytes();
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(fileBytes));
            
            int width = bufferedImage != null ? bufferedImage.getWidth() : 0;
            int height = bufferedImage != null ? bufferedImage.getHeight() : 0;

            // Determine subPath based on displayOrder (null/0 = main image, >0 = gallery)
            String subPath = (metadata.getDisplayOrder() == null || metadata.getDisplayOrder() == 0) 
                    ? null 
                    : "gallery";

            // Create StorageFile
            StorageFile storageFile = StorageFile.builder()
                    .inputStream(new ByteArrayInputStream(fileBytes))
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .entityType(metadata.getEntityType())
                    .entityId(metadata.getEntityId())
                    .subPath(subPath)
                    .build();

            // Store file
            StorageResult storageResult = storageService.store(storageFile);

            if (!storageResult.isSuccess()) {
                return ImageUploadResponse.builder()
                        .success(false)
                        .message(storageResult.getMessage())
                        .build();
            }

            // Determine display order
            Integer displayOrder = metadata.getDisplayOrder();
            if (displayOrder == null) {
                displayOrder = imageGalleryRepository.getMaxDisplayOrder(
                        metadata.getEntityType(), 
                        metadata.getEntityId()
                ) + 1;
            }

            // Save to database
            ImageGallery imageGallery = ImageGallery.builder()
                    .entityType(metadata.getEntityType())
                    .entityId(metadata.getEntityId())
                    .imageUrl(storageResult.getFileUrl())
                    .fileName(storageResult.getFileName())
                    .displayOrder(displayOrder)
                    .altText(metadata.getAltText())
                    .description(metadata.getDescription())
                    .fileSizeKb((int) (storageResult.getFileSizeBytes() / 1024))
                    .widthPx(width)
                    .heightPx(height)
                    .mimeType(storageResult.getContentType())
                    .uploadedByUserId(metadata.getUploadedByUserId())
                    .build();

            imageGalleryRepository.save(imageGallery);

            log.info("Image uploaded successfully: {} for {} {}", 
                    storageResult.getFileUrl(), 
                    metadata.getEntityType(), 
                    metadata.getEntityId());

            return ImageUploadResponse.builder()
                    .imageUrl(storageResult.getFileUrl())
                    .fileName(storageResult.getFileName())
                    .fileSizeKb((int) (storageResult.getFileSizeBytes() / 1024))
                    .widthPx(width)
                    .heightPx(height)
                    .mimeType(storageResult.getContentType())
                    .success(true)
                    .message("Image uploaded successfully")
                    .build();

        } catch (IOException e) {
            log.error("Failed to process image file", e);
            return ImageUploadResponse.builder()
                    .success(false)
                    .message("Failed to process image: " + e.getMessage())
                    .build();
        } catch (StorageException e) {
            log.error("Storage error", e);
            return ImageUploadResponse.builder()
                    .success(false)
                    .message("Storage error: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during image upload", e);
            return ImageUploadResponse.builder()
                    .success(false)
                    .message("Unexpected error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageGalleryResponse> getImagesForEntity(EntityType entityType, Long entityId) {
        List<ImageGallery> images = imageGalleryRepository
                .findByEntityTypeAndEntityIdOrderByDisplayOrderAsc(entityType, entityId);
        
        return imageGalleryMapper.toResponseList(images);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageGalleryResponse getImageById(Long imageId) {
        ImageGallery image = imageGalleryRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));
        
        return imageGalleryMapper.toResponse(image);
    }

    @Override
    @Transactional
    public ImageGalleryResponse updateImageMetadata(Long imageId, ImageGalleryRequest request) {
        ImageGallery image = imageGalleryRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));

        // Update only metadata fields
        if (request.getAltText() != null) {
            image.setAltText(request.getAltText());
        }
        if (request.getDescription() != null) {
            image.setDescription(request.getDescription());
        }
        if (request.getDisplayOrder() != null) {
            image.setDisplayOrder(request.getDisplayOrder());
        }

        ImageGallery updatedImage = imageGalleryRepository.save(image);
        log.info("Image metadata updated: {}", imageId);

        return imageGalleryMapper.toResponse(updatedImage);
    }

    @Override
    @Transactional
    public boolean deleteImage(Long imageId) {
        ImageGallery image = imageGalleryRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));

        // Delete from storage
        boolean storageDeleted = storageService.delete(image.getImageUrl());
        
        if (!storageDeleted) {
            log.warn("Failed to delete image from storage, but will remove from database: {}", 
                    image.getImageUrl());
        }

        // Delete from database
        imageGalleryRepository.delete(image);
        log.info("Image deleted: {}", imageId);

        return true;
    }

    @Override
    @Transactional
    public int deleteAllImagesForEntity(EntityType entityType, Long entityId) {
        // Get all images
        List<ImageGallery> images = imageGalleryRepository
                .findByEntityTypeAndEntityId(entityType, entityId);

        if (images.isEmpty()) {
            return 0;
        }

        // Delete from storage
        int storageDeletedCount = storageService.deleteAllForEntity(entityType, entityId);
        log.info("Deleted {} files from storage for {} {}", storageDeletedCount, entityType, entityId);

        // Delete from database
        imageGalleryRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
        
        int deletedCount = images.size();
        log.info("Deleted {} image records from database for {} {}", deletedCount, entityType, entityId);

        return deletedCount;
    }

    @Override
    @Transactional
    public void reorderImages(EntityType entityType, Long entityId, List<Long> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            ImageGallery image = imageGalleryRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

            // Verify image belongs to the entity
            if (!image.getEntityType().equals(entityType) || !image.getEntityId().equals(entityId)) {
                throw new IllegalArgumentException("Image does not belong to this entity");
            }

            image.setDisplayOrder(i + 1);
            imageGalleryRepository.save(image);
        }

        log.info("Reordered {} images for {} {}", imageIds.size(), entityType, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countImagesForEntity(EntityType entityType, Long entityId) {
        return imageGalleryRepository.countByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddMoreImages(EntityType entityType, Long entityId) {
        long currentCount = countImagesForEntity(entityType, entityId);
        int limit = IMAGE_LIMITS.getOrDefault(entityType, 10);
        
        return currentCount < limit;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Allowed types: " + 
                    String.join(", ", ALLOWED_CONTENT_TYPES));
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !hasValidExtension(originalFilename)) {
            throw new IllegalArgumentException("Invalid file extension");
        }
    }

    private boolean hasValidExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg") ||
               lowerFilename.endsWith(".jpeg") ||
               lowerFilename.endsWith(".png") ||
               lowerFilename.endsWith(".webp") ||
               lowerFilename.endsWith(".gif");
    }
}
