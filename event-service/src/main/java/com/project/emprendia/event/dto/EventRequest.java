package com.project.emprendia.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
}
