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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Digital Ocean Spaces implementation of StorageService.
 * Uses AWS S3 SDK for compatibility with Digital Ocean Spaces.
 * Used for production and test environments.
 * 
 * Structure in Digital Ocean Spaces:
 * bucket-name/
 *   users/{userId}/profile.jpg
 *   users/{userId}/gallery/img1.jpg
 *   entrepreneurships/{entrepreneurshipId}/logo.jpg
 *   entrepreneurships/{entrepreneurshipId}/gallery/img1.jpg
 *   events/{eventId}/cover.jpg
 *   events/{eventId}/gallery/img1.jpg
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "cloud")
public class CloudStorageService implements StorageService {

    private S3Client s3Client;

    @Value("${storage.cloud.endpoint}")
    private String endpoint;

    @Value("${storage.cloud.region:us-east-1}")
    private String region;

    @Value("${storage.cloud.access-key}")
    private String accessKey;

    @Value("${storage.cloud.secret-key}")
    private String secretKey;

    @Value("${storage.cloud.bucket-name}")
    private String bucketName;

    @Value("${storage.cloud.base-url:}")
    private String baseUrl;

    @PostConstruct
    public void initializeS3Client() {
        try {
            log.info("🚀 Initializing CloudStorageService...");

            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();

            // If base URL is not provided, construct it from endpoint and bucket
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = endpoint + "/" + bucketName;
            }

            log.info("✅ Cloud storage (Digital Ocean Spaces) initialized successfully");
            log.info("📦 Bucket: {}, Endpoint: {}", bucketName, endpoint);

        } catch (Exception e) {
            log.error("Failed to initialize cloud storage", e);
            throw new StorageException("Failed to initialize cloud storage", e);
        }
    }

    @PreDestroy
    public void closeS3Client() {
        if (s3Client != null) {
            s3Client.close();
            log.info("S3 client closed");
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

            // Build key (path in bucket)
            String key = buildPath(
                storageFile.getEntityType(),
                storageFile.getEntityId(),
                storageFile.getSubPath(),
                uniqueFileName
            );

            // Prepare request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(storageFile.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // Make publicly accessible
                    .build();

            // Upload file
            s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(storageFile.getInputStream(), storageFile.getSize())
            );

            // Build public URL
            String fileUrl = baseUrl + "/" + key;

            log.info("File uploaded successfully to Digital Ocean Spaces: {}", fileUrl);

            return StorageResult.builder()
                    .fileUrl(fileUrl)
                    .storagePath(key)
                    .fileName(uniqueFileName)
                    .contentType(storageFile.getContentType())
                    .fileSizeBytes(storageFile.getSize())
                    .success(true)
                    .message("File uploaded successfully to cloud storage")
                    .build();

        } catch (S3Exception e) {
            log.error("S3 error while storing file", e);
            throw new StorageException("Failed to store file in cloud storage: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Failed to store file", e);
            throw new StorageException("Failed to store file: " + storageFile.getOriginalFileName(), e);
        }
    }

    @Override
    public boolean delete(String fileUrl) {
        try {
            // Extract key from URL
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted from Digital Ocean Spaces: {}", fileUrl);
            return true;

        } catch (S3Exception e) {
            log.error("S3 error while deleting file: {}", fileUrl, e);
            return false;
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            return false;
        }
    }

    @Override
    public int deleteAllForEntity(EntityType entityType, Long entityId) {
        try {
            String prefix = getEntityBasePath(entityType, entityId) + "/";
            
            // List all objects with the prefix
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
            
            int deletedCount = 0;
            for (S3Object s3Object : listResponse.contents()) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build();
                
                s3Client.deleteObject(deleteRequest);
                deletedCount++;
            }

            log.info("Deleted {} files for {} with id {} from Digital Ocean Spaces", 
                    deletedCount, entityType, entityId);
            return deletedCount;

        } catch (S3Exception e) {
            log.error("S3 error while deleting files for entity: {} {}", entityType, entityId, e);
            throw new StorageException("Failed to delete files for entity", e);
        }
    }

    @Override
    public InputStream getFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObject(getObjectRequest);

        } catch (NoSuchKeyException e) {
            throw new StorageException("File not found: " + fileUrl, e);
        } catch (S3Exception e) {
            log.error("S3 error while getting file: {}", fileUrl, e);
            throw new StorageException("Failed to get file from cloud storage", e);
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("S3 error while checking file existence: {}", fileUrl, e);
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
            return basePath + "/" + subPath + "/" + fileName;
        }
        
        return basePath + "/" + fileName;
    }

    private String getEntityBasePath(EntityType entityType, Long entityId) {
        return switch (entityType) {
            case USER -> "users/" + entityId;
            case ENTREPRENEURSHIP -> "entrepreneurships/" + entityId;
            case EVENT -> "events/" + entityId;
        };
    }

    private String extractKeyFromUrl(String fileUrl) {
        // Remove base URL to get the key
        if (fileUrl.startsWith(baseUrl)) {
            return fileUrl.substring(baseUrl.length() + 1);
        }
        
        // If URL doesn't start with baseUrl, try to extract after bucket name
        int bucketIndex = fileUrl.indexOf(bucketName);
        if (bucketIndex != -1) {
            return fileUrl.substring(bucketIndex + bucketName.length() + 1);
        }
        
        throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
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
