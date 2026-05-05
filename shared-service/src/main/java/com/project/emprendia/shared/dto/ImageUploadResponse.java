package com.project.emprendia.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing the result of an image upload operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    private String imageUrl;
    private String fileName;
    private Integer fileSizeKb;
    private Integer widthPx;
    private Integer heightPx;
    private String mimeType;
    private String message;
    private boolean success;
}
