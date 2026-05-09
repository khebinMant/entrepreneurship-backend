package com.project.emprendia.shared.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a storage operation containing file metadata and URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageResult {
    
    private String fileUrl;        // URL completa para acceso HTTP
    private String storagePath;    // Path relativo en el storage (users/1/file.jpg)
    private String fileName;
    private String contentType;
    private long fileSizeBytes;
    private boolean success;
    private String message;
}
