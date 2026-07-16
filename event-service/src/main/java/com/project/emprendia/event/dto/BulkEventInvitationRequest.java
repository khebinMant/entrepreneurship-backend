package com.project.emprendia.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkEventInvitationRequest {

    @Valid
    @NotEmpty
    private List<EventInvitationRequest> invitations;
}
