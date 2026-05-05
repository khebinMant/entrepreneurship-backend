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
    
    private String fileUrl;
    private String fileName;
    private String contentType;
    private long fileSizeBytes;
    private boolean success;
    private String message;
}
