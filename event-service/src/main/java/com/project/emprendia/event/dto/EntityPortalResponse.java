package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityPortalResponse {
    private Long portalId;
    private Long entityId;
    private String subdomain;
    private Long themeId;
    private String themeName;
    private Boolean isActive;
    private String htmlContent;
    private LocalDateTime createdAt;
}
