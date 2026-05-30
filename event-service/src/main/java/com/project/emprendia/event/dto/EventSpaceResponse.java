package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSpaceResponse {
    private Long eventSpaceId;
    private Long eventId;
    private String eventName;
    private String spaceCode;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
}

