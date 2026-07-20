package com.project.emprendia.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContactResponse {
    private Long userContactId;
    private Long userId;
    private Long contactTypeId;
    private String contactValue;
    private Boolean isPrimary;
}
