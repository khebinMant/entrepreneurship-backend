package com.project.emprendia.event.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
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
    private LocalDateTime createdAt;
}
