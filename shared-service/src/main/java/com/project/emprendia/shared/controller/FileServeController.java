package com.project.emprendia.shared.controller;

import com.project.emprendia.shared.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

/**
 * Controller for serving static files from local storage.
 * Only active when storage.type=local.
 * 
 * Endpoints:
 * - GET /api/files/users/{userId}/{fileName}
 * - GET /api/files/users/{userId}/gallery/{fileName}
 * - GET /api/files/entrepreneurships/{id}/{fileName}
 * - GET /api/files/entrepreneurships/{id}/gallery/{fileName}
 * - GET /api/files/events/{id}/{fileName}
 * - GET /api/files/events/{id}/gallery/{fileName}
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class FileServeController {

    private final StorageService storageService;

    /**
     * Serve user profile image
     */
    @GetMapping("/users/{userId}/{fileName:.+}")
    public ResponseEntity<Resource> serveUserFile(
            @PathVariable Long userId,
            @PathVariable String fileName) {

        return serveFile("users/" + userId + "/" + fileName);
    }

    /**
     * Serve user gallery image
     */
    @GetMapping("/users/{userId}/gallery/{fileName:.+}")
    public ResponseEntity<Resource> serveUserGalleryFile(
            @PathVariable Long userId,
            @PathVariable String fileName) {

        return serveFile("users/" + userId + "/gallery/" + fileName);
    }

    /**
     * Serve entrepreneurship logo
     */
    @GetMapping("/entrepreneurships/{entrepreneurshipId}/{fileName:.+}")
    public ResponseEntity<Resource> serveEntrepreneurshipFile(
            @PathVariable Long entrepreneurshipId,
            @PathVariable String fileName) {

        return serveFile("entrepreneurships/" + entrepreneurshipId + "/" + fileName);
    }

    /**
     * Serve entrepreneurship gallery image
     */
    @GetMapping("/entrepreneurships/{entrepreneurshipId}/gallery/{fileName:.+}")
    public ResponseEntity<Resource> serveEntrepreneurshipGalleryFile(
            @PathVariable Long entrepreneurshipId,
            @PathVariable String fileName) {

        return serveFile("entrepreneurships/" + entrepreneurshipId + "/gallery/" + fileName);
    }

    /**
     * Serve event cover image
     */
    @GetMapping("/events/{eventId}/{fileName:.+}")
    public ResponseEntity<Resource> serveEventFile(
            @PathVariable Long eventId,
            @PathVariable String fileName) {

        return serveFile("events/" + eventId + "/" + fileName);
    }

    /**
     * Serve event gallery image
     */
    @GetMapping("/events/{eventId}/gallery/{fileName:.+}")
    public ResponseEntity<Resource> serveEventGalleryFile(
            @PathVariable Long eventId,
            @PathVariable String fileName) {

        return serveFile("events/" + eventId + "/gallery/" + fileName);
    }

    /**
     * Common method to serve files
     */
    private ResponseEntity<Resource> serveFile(String relativePath) {
        try {
            // Build full URL (this is a bit hacky but works for local storage)
            String fileUrl = "http://localhost:8084/api/files/" + relativePath;
            
            InputStream inputStream = storageService.getFile(fileUrl);
            Resource resource = new InputStreamResource(inputStream);

            // Determine content type from file extension
            String contentType = determineContentType(relativePath);

            log.debug("Serving file: {}", relativePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + getFileName(relativePath) + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error serving file: {}", relativePath, e);
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        } else if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerPath.endsWith(".webp")) {
            return "image/webp";
        }
        
        return "application/octet-stream";
    }

    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash != -1 ? path.substring(lastSlash + 1) : path;
    }
}
