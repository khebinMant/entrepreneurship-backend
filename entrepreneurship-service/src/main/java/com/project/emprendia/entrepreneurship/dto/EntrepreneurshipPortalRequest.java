package com.project.emprendia.entrepreneurship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrepreneurshipPortalRequest {

    @NotNull(message = "Entrepreneurship ID is required")
    private Long entrepreneurshipId;

    @NotBlank(message = "Subdomain is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Subdomain must contain only lowercase letters, numbers and hyphens")
    private String subdomain;

    @NotNull(message = "Theme ID is required")
    private Long themeId;

    private Boolean isActive;
}

