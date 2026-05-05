package com.project.emprendia.shared.storage;

import com.project.emprendia.shared.enums.EntityType;

import java.io.InputStream;

/**
 * Interface defining storage operations for images.
 * Implementations can be local storage (development) or cloud storage (production).
 */
public interface StorageService {

    /**
     * Store a file and return its URL and metadata
     * 
     * @param storageFile File to store with metadata
     * @return StorageResult containing URL and metadata
     */
    StorageResult store(StorageFile storageFile);

    /**
     * Delete a file by its URL
     * 
     * @param fileUrl URL of the file to delete
     * @return true if deletion was successful
     */
    boolean delete(String fileUrl);

    /**
     * Delete all files for a specific entity
     * 
     * @param entityType Type of entity (USER, ENTREPRENEURSHIP, EVENT)
     * @param entityId ID of the entity
     * @return number of files deleted
     */
    int deleteAllForEntity(EntityType entityType, Long entityId);

    /**
     * Get file as InputStream
     * 
     * @param fileUrl URL of the file
     * @return InputStream of the file
     */
    InputStream getFile(String fileUrl);

    /**
     * Check if a file exists
     * 
     * @param fileUrl URL of the file
     * @return true if file exists
     */
    boolean exists(String fileUrl);

    /**
     * Generate a unique file name to avoid collisions
     * 
     * @param originalFileName Original file name
     * @return Generated unique file name
     */
    String generateUniqueFileName(String originalFileName);

    /**
     * Build the storage path for a file
     * 
     * @param entityType Type of entity
     * @param entityId ID of the entity
     * @param subPath Subpath (gallery, profile, logo, cover)
     * @param fileName File name
     * @return Complete path
     */
    String buildPath(EntityType entityType, Long entityId, String subPath, String fileName);
}
