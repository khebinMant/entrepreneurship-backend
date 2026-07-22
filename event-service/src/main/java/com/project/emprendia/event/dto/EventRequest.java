package com.project.emprendia.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequest {

    @NotNull
    private Long createdByUserId;

    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;

    @NotNull
    private Long eventTypeId;

    @NotNull
    private Long eventVisibilityId;

    @NotNull
    private Boolean isPaid;

    private BigDecimal price;
    private Integer maxAttendees;
    private Integer maxEntrepreneurships;
    private String virtualLink;

    @NotNull
    private LocalDateTime startDatetime;

    @NotNull
    private LocalDateTime endDatetime;

    private Long countryId;
    private Long provinceId;
    private Long cityId;
    private String addressLine;
    private String mapsUrl;

    private List<SocialLinkItem> socialLinks;
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
    public static class PortalItem {
        private String subdomain;
        private Long themeId;
        private Boolean isActive;
        private String htmlContent;
    }
}
