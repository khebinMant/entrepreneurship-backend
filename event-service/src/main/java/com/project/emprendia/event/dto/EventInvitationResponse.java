package com.project.emprendia.event.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventInvitationResponse {
    private Long invitationId;
    private Long eventId;
    private Long entrepreneurshipId;
    private Long eventSpaceId;
    private Long invitationStatusId;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
}
