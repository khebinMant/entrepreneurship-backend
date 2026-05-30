package com.project.emprendia.event.dto;

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
public class EventSpaceRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Space code is required")
    private String spaceCode;

    private Boolean isAvailable;
}

