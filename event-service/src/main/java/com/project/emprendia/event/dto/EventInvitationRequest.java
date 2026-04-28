package com.project.emprendia.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventInvitationRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private Long entrepreneurshipId;

    private Long eventSpaceId;

    @NotNull
    private Long invitationStatusId;
}
