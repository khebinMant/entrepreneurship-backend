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
public class EventParticipantResponse {
    private Long eventParticipantId;
    private Long eventId;
    private String eventName;
    private Long entrepreneurshipId;
    private String entrepreneurshipName;
    private String spaceCode;
    private Long participationStatusId;
    private String participationStatusName;
    private LocalDateTime invitedAt;
    private LocalDateTime respondedAt;
}

