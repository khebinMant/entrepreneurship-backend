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
public class EntrepreneurshipSocialLinkResponse {
    private Long socialLinkId;
    private Long entrepreneurshipId;
    private String entrepreneurshipName;
    private Long socialPlatformId;
    private String socialPlatformName;
    private String url;
    private LocalDateTime createdAt;
}

