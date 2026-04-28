package com.project.emprendia.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserContactResponse {
    private Long userContactId;
    private Long contactTypeId;
    private String contactValue;
    private Boolean isPrimary;
}
