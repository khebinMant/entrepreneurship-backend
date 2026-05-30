package com.project.emprendia.entrepreneurship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipPortalResponse {
    private Long portalId;
    private Long entrepreneurshipId;
    private String entrepreneurshipName;
    private String subdomain;
    private Long themeId;
    private String themeName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

