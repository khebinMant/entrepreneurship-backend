package com.project.emprendia.shared.storage.impl;

import com.project.emprendia.shared.enums.EntityType;
import com.project.emprendia.shared.exception.StorageException;
import com.project.emprendia.shared.storage.StorageFile;
import com.project.emprendia.shared.storage.StorageResult;
import com.project.emprendia.shared.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Local file system implementation of StorageService.
 * Used for development environment.
 * 
 * Structure:
 * {base-path}/
 *   users/{userId}/profile.jpg
 *   users/{userId}/gallery/img1.jpg
 *   entrepreneurships/{entrepreneurshipId}/logo.jpg
 *   entrepreneurships/{entrepreneurshipId}/gallery/img1.jpg
 *   events/{eventId}/cover.jpg
 *   events/{eventId}/gallery/img1.jpg
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final Path rootLocation;
    private final String baseUrl;

    public LocalStorageService(
            @Value("${storage.local.path:./uploads}") String uploadPath,
            @Value("${storage.local.base-url:http://localhost:8084/api/files}") String baseUrl) {
        this.rootLocation = Paths.get(uploadPath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Files.createDirectories(rootLocation);
            log.info("🚀 LocalStorageService initialized");
            log.info("📁 Local storage path: {}", rootLocation);
            log.info("🌐 Base URL: {}", baseUrl);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public StorageResult store(StorageFile storageFile) {
        try {
            if (storageFile.getInputStream() == null) {
                throw new StorageException("Input stream is null");
            }

            // Generate unique filename
            String uniqueFileName = generateUniqueFileName(storageFile.getOriginalFileName());
            
            // Build complete path
            String relativePath = buildPath(
                storageFile.getEntityType(),
                storageFile.getEntityId(),
                storageFile.getSubPath(),
                uniqueFileName
            );

            Path targetPath = rootLocation.resolve(relativePath);
            
            // Create directories if they don't exist
            Files.createDirectories(targetPath.getParent());

            // Copy file
            try (InputStream inputStream = storageFile.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Build URL
            String fileUrl = baseUrl + "/" + relativePath.replace("\\", "/");

            log.info("File stored successfully: {}", fileUrl);

            return StorageResult.builder()
                    .fileUrl(fileUrl)
                    .storagePath(relativePath.replace("\\", "/"))
                    .fileName(uniqueFileName)
                    .contentType(storageFile.getContentType())
                    .fileSizeBytes(storageFile.getSize())
                    .success(true)
                    .message("File stored successfully")
                    .build();

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new StorageException("Failed to store file: " + storageFile.getOriginalFileName(), e);
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            // Extract relative path from URL
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = rootLocation.resolve(relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted: {}", fileUrl);
                return true;
            }
            
            log.warn("File not found for deletion: {}", fileUrl);
            return false;

        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public int deleteAllForEntity(EntityType entityType, Long entityId) {
        try {
            String entityPath = getEntityBasePath(entityType, entityId);
            Path directoryPath = rootLocation.resolve(entityPath);

            if (!Files.exists(directoryPath)) {
                log.warn("Directory not found: {}", directoryPath);
                return 0;
            }

            int deletedCount = deleteDirectory(directoryPath);
            log.info("Deleted {} files for {} with id {}", deletedCount, entityType, entityId);
            return deletedCount;

        } catch (IOException e) {
            log.error("Failed to delete files for entity: {} {}", entityType, entityId, e);
            throw new StorageException("Failed to delete files for entity", e);
        }
    }

    private int deleteDirectory(Path directory) throws IOException {
        int count = 0;
        try (Stream<Path> walk = Files.walk(directory)) {
            for (Path path : walk.sorted((a, b) -> b.compareTo(a)).toList()) {
                if (Files.isRegularFile(path)) {
                    Files.delete(path);
                    count++;
                } else if (Files.isDirectory(path)) {
                    Files.delete(path);
                }
            }
        }
        return count;
    }

    @Override
    public InputStream getFile(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = rootLocation.resolve(relativePath);

            if (!Files.exists(filePath)) {
                throw new StorageException("File not found: " + fileUrl);
            }

            return Files.newInputStream(filePath);

        } catch (IOException e) {
            throw new StorageException("Failed to read file: " + fileUrl, e);
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = rootLocation.resolve(relativePath);
            return Files.exists(filePath);
        } catch (Exception e) {
            log.error("Error checking file existence: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public String generateUniqueFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);
        
        return timestamp + "_" + uuid + extension;
    }

    @Override
    public String buildPath(EntityType entityType, Long entityId, String subPath, String fileName) {
        String basePath = getEntityBasePath(entityType, entityId);
        
        if (subPath != null && !subPath.isEmpty()) {
            return Paths.get(basePath, subPath, fileName).toString();
        }
        
        return Paths.get(basePath, fileName).toString();
    }

    private String getEntityBasePath(EntityType entityType, Long entityId) {
        return switch (entityType) {
            case USER -> "users/" + entityId;
            case ENTREPRENEURSHIP -> "entrepreneurships/" + entityId;
            case EVENT -> "events/" + entityId;
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex);
    }
}
