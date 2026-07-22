package com.project.emprendia.entrepreneurship.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EntrepreneurshipRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;
    private String logoUrl;

    @NotNull
    private Boolean isPhysical;

    @NotNull
    private Boolean isDigital;

    private List<SocialLinkItem> socialLinks;
    private List<LocationItem> locations;
    private PortalItem portal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialLinkItem {
        @NotNull
        private Long socialPlatformId;
        @NotBlank
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationItem {
        @NotNull
        private Long countryId;
        @NotNull
        private Long provinceId;
        @NotNull
        private Long cityId;
        private Long parishId;
        private String addressLine;
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        private BigDecimal latitude;
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        private BigDecimal longitude;
        private String mapsUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortalItem {
        @NotBlank
        private String subdomain;
        private Long themeId;
        private Boolean isActive;
        private String htmlContent;
    }
}
