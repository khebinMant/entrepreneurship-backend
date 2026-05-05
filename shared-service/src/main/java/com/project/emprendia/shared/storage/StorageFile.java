package com.project.emprendia.shared.storage;

import com.project.emprendia.shared.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * Represents metadata and content of a file to be stored.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageFile {
    
    private InputStream inputStream;
    private String originalFileName;
    private String contentType;
    private long size;
    private EntityType entityType;
    private Long entityId;
    private String subPath; // e.g., "gallery", "profile", "logo", "cover"
}
