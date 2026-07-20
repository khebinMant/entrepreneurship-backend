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
public class EntitySocialLinkResponse {
    private Long socialLinkId;
    private Long entityId;
    private Long socialPlatformId;
    private String socialPlatformName;
    private String url;
    private LocalDateTime createdAt;
}
