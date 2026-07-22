package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long eventId;
    private Long createdByUserId;
    private String name;
    private String description;
    private Long eventTypeId;
    private Long eventVisibilityId;
    private Boolean isPaid;
    private BigDecimal price;
    private Integer maxAttendees;
    private Integer maxEntrepreneurships;
    private String virtualLink;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Long countryId;
    private Long provinceId;
    private Long cityId;
    private String addressLine;
    private String mapsUrl;
    private LocalDateTime createdAt;

    private UserBasicResponse createdByUser;

    private CatalogueValueResponse eventType;
    private CatalogueValueResponse eventVisibility;

    private CatalogueValueResponse country;
    private CatalogueValueResponse province;
    private CatalogueValueResponse city;

    private String imageUrl;
    private Long imageId;

    private List<EntitySocialLinkResponse> socialLinks;
    private EntityPortalResponse portal;
}
