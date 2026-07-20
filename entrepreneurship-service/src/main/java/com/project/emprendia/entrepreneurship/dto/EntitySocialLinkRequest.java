package com.project.emprendia.entrepreneurship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitySocialLinkRequest {
    
    @NotNull(message = "Entity ID is required")
    private Long entityId;
    
    @NotNull(message = "Social platform ID is required")
    private Long socialPlatformId;
    
    @NotBlank(message = "URL is required")
    private String url;
}
